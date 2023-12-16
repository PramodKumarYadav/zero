package org.powertester.data;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class TestData {
  private final Map<String, String> headerValueMap;
  private final Map<String, String> headerTypeMap;

  public TestData(Map<String, String> headerValueMap, Map<String, String> headerTypeMap) {
    this.headerValueMap = headerValueMap;
    this.headerTypeMap = headerTypeMap;
  }

  public TestData() {
    this.headerValueMap = new LinkedHashMap<>();
    this.headerTypeMap = new LinkedHashMap<>();
  }

  public TestData setValueToKeys(String value, String... keys) {
    return setValueToKeys(value, "DEFAULT", keys);
  }

  public TestData setValueToKeys(String value, String type, String... keys) {
    for (String key : keys) {
      headerValueMap.put(key, value);
      headerTypeMap.put(key, type);
    }
    return this;
  }

  public String getValue(String key) {
    return headerValueMap.get(key);
  }

  public String getType(String key) {
    return headerTypeMap.get(key);
  }

  public void toString(String... keys) {
    for (String key : keys) {
      log.info("Key: {}, Value: {}, Type: {}", key, getValue(key), getType(key));
    }
  }
}
