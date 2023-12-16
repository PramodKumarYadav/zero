package unittests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.powertester.annotations.CSVToTestDataMap;
import org.powertester.data.TestData;

@Slf4j
class CSVAggregatorUnitTests {

  @ParameterizedTest(name = "Test data: {0}")
  @CsvFileSource(files = "src/test/resources/testdata/test-data.csv", numLinesToSkip = 1)
  void parseCSVToTestDataMap(@CSVToTestDataMap TestData testData) {
    //    log.info(testData.getHeaderValueMap().toString());
    //    log.info(testData.getHeaderTypeMap().toString());
  }
}
