package unittests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.powertester.annotations.CSVToTestDataMap;
import org.powertester.data.TestData;

class CSVAggregatorUnitTests {
  @ParameterizedTest(name = "Test > {0} : {1}: {2}")
  @CsvFileSource(files = "src/test/resources/testdata/test-data.csv", numLinesToSkip = 1)
  void parseCSVToTestDataMap(@CSVToTestDataMap TestData testData) {
    // Add your test here

  }
}
