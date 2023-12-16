package org.powertester.factories;

import java.security.SecureRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumberFactory {
  private NumberFactory() {
    throw new IllegalStateException("Utility class");
  }

  private static final SecureRandom secureRandom = new SecureRandom();

  public static String getNumberForTag(String nDigits) {
    int length = Integer.parseInt(nDigits.replace("digits", ""));
    log.debug("Generating random number with {} digits", length);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(secureRandom.nextInt(1, 10));
    }
    return sb.toString();
  }
}
