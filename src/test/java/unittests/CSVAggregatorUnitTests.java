package unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.powertester.annotations.CSVToTestDataMap;
import org.powertester.data.TestData;

class CSVAggregatorUnitTests {
  @ParameterizedTest(name = "Test > {0} : {1}: {2}")
  @CsvFileSource(files = "src/test/resources/testdata/test-data.csv", numLinesToSkip = 1)
  void parseCSVToTestDataMap(@CSVToTestDataMap TestData testData) {
    // Add your test here
    assertAll(
        () -> assertEquals("Common comment", testData.getValue("COMMENT")),
        () -> assertEquals("META", testData.getType("COMMENT")));
  }
}
