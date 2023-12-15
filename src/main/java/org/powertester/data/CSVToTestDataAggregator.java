package org.powertester.data;

import com.typesafe.config.Config;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.powertester.config.TestEnvFactory;

/**
 * 1. Trim Headers ✅. 2. Trim Values ✅. 3. Trim Types ✅. 4. Don't add headers with empty values ✅.
 * 5. Add default type to headers with empty types ✅. 6. Replace dynamic header tags with actual
 * values ✅. - Dynamic headers are ones with $ in them (ex: $IN, $OUT, $INOUT). - Return the tag
 * itself if actual value is not found for a dynamic header. Since a dynamic header can also contain
 * fixed static values (01-01-2021) or amount 1.01. 7. Return object as a TestData object ✅.
 */
@Slf4j
public class CSVToTestDataAggregator implements ArgumentsAggregator {
  private static final Config CONFIG = TestEnvFactory.getInstance().getConfig();
  private static final String CSV_DELIMITER = CONFIG.getString("CSV_DELIMITER");
  private final AtomicBoolean isFileRead = new AtomicBoolean(false);
  private List<String> headers = new ArrayList<>();
  private Map<String, String> headerTypeMap = new HashMap<>();
  private Map<String, String> headerValueMap = new HashMap<>();
  private Map<String, String> tagValueMap = new HashMap<>();

  @Override
  public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) {
    readFileOnlyOnceAndSetNonEmptyHeaders(accessor, context);
    setHeaderValueMap(accessor);
    setHeaderValueMapForDynamicValues();

    return new TestData(headerValueMap, headerTypeMap);
  }

  private void readFileOnlyOnceAndSetNonEmptyHeaders(
      ArgumentsAccessor accessor, ParameterContext context) {
    if (isFileRead.compareAndSet(false, true)) {
      Path csvFilePath = getCSVFilePath(context);

      List<String> rawHeaders = getHeadersFromFirstRowOfCSVFile(csvFilePath);

      List<String> rawHeadersWithNonEmptyValues =
          getHeadersWithNonEmptyValues(accessor, rawHeaders);

      setHeadersAndTypes(rawHeadersWithNonEmptyValues);

      log.debug("Headers: {}", headers);
      log.debug("Header Types: {}", headerTypeMap);
    }
  }

  private Path getCSVFilePath(ParameterContext context) {
    return Path.of(
        context.getDeclaringExecutable().getAnnotation(CsvFileSource.class).resources()[0]);
  }

  private List<String> getHeadersFromFirstRowOfCSVFile(Path csvFilePath) {
    try {
      return Arrays.stream(Files.readAllLines(csvFilePath).get(0).split(CSV_DELIMITER))
          .map(String::trim)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException("Exception while reading CSV file: " + csvFilePath);
    }
  }

  private static List<String> getHeadersWithNonEmptyValues(
      ArgumentsAccessor accessor, List<String> rawHeaders) {
    return rawHeaders.stream()
        .filter(header -> accessor.getString(rawHeaders.indexOf(header)) != null)
        .filter(header -> !accessor.getString(rawHeaders.indexOf(header)).isEmpty())
        .collect(Collectors.toList());
  }

  private void setHeadersAndTypes(List<String> rawHeaders) {
    for (String rawHeader : rawHeaders) {
      String[] headerAndType = rawHeader.split(":");
      String header = headerAndType[0].trim();
      String type = headerAndType.length > 1 ? headerAndType[1].trim() : "DEFAULT";
      if (header.isEmpty()) {
        continue;
      }
      headers.add(header);
      headerTypeMap.put(header, type);
    }
  }

  private void setHeaderValueMap(ArgumentsAccessor accessor) {

    headers.stream()
        .filter(header -> accessor.getString(headers.indexOf(header)) != null)
        .forEach(
            header -> {
              String value = accessor.getString(headers.indexOf(header)).trim();
              headerValueMap.put(header, value);
            });
  }

  private void setHeaderValueMapForDynamicValues() {
    headerValueMap.keySet().stream()
        .filter(header -> headerTypeMap.get(header).contains("$"))
        .forEach(
            header -> {
              String tag = headerValueMap.get(header);
              headerValueMap.put(
                  header, tagValueMap.computeIfAbsent(tag, value -> TagsFactory.getValueFor(tag)));
            });
  }
}
