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
  private final List<String> rawHeaders = new ArrayList<>();
  private final Map<String, String> originalCSVMap = new HashMap<>();
  private final Map<String, String> headerValueMap = new HashMap<>();
  private final Map<String, String> headerTypeMap = new HashMap<>();
  private final Map<String, String> tagValueMap = new HashMap<>();

  @Override
  public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) {
    readCSVFileAndSetHeadersOnlyOnceForTheWholeParameterizedTest(context);
    convertCSVToMap(accessor, rawHeaders);
    setHeaderTypeAndValueMapsForNonEmptyValues();
    setHeaderValueMapForDynamicValues();

    log.info("HeaderValueMap: {}", headerValueMap);
    log.info("HeaderTypeMap: {}", headerTypeMap);
    return new TestData(headerValueMap, headerTypeMap);
  }

  private void readCSVFileAndSetHeadersOnlyOnceForTheWholeParameterizedTest(
      ParameterContext context) {
    if (isFileRead.compareAndSet(false, true)) {
      Path csvFilePath =
          Path.of(
              context.getDeclaringExecutable().getAnnotation(CsvFileSource.class).resources()[0]);
      try {
        List<String> rawHeadersIncludingMetadata =
            Arrays.stream(Files.readAllLines(csvFilePath).get(0).split(CSV_DELIMITER))
                .map(String::trim)
                .collect(Collectors.toList());
        rawHeaders.addAll(rawHeadersIncludingMetadata);
      } catch (IOException exception) {
        exception.printStackTrace();
        throw new IllegalStateException("Exception while reading CSV file: " + csvFilePath);
      }
    }
  }

  private void convertCSVToMap(ArgumentsAccessor accessor, List<String> rawHeaders) {
    rawHeaders.forEach(
        header -> {
          String value = accessor.getString(rawHeaders.indexOf(header));
          originalCSVMap.put(header, value);
        });
  }

  private void setHeaderTypeAndValueMapsForNonEmptyValues() {
    originalCSVMap
        .keySet()
        .forEach(
            rawHeader -> {
              String[] headerAndType = rawHeader.split(":");

              String headerName = headerAndType[0].trim();
              String type = headerAndType.length > 1 ? headerAndType[1].trim() : "DEFAULT";
              String value =
                  originalCSVMap.get(rawHeader) != null ? originalCSVMap.get(rawHeader).trim() : "";
              if (!value.isEmpty()) {
                headerValueMap.put(headerName, value);
                headerTypeMap.put(headerName, type);
              }
            });
  }

  private void setHeaderValueMapForDynamicValues() {
    headerValueMap.keySet().stream()
        .filter(header -> headerTypeMap.get(header).contains("$"))
        .forEach(
            header -> {
              String tag = headerValueMap.get(header);
              String dynamicValue =
                  tagValueMap.computeIfAbsent(tag, value -> TagsFactory.getValueFor(tag));
              headerValueMap.put(header, dynamicValue);
            });
  }
}
