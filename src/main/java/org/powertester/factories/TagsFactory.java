package org.powertester.factories;

import java.util.Locale;
import java.util.UUID;

public class TagsFactory {
  private TagsFactory() {
    throw new IllegalStateException("Utility class");
  }

  public static String getValueFor(String tag) {
    if (tag.endsWith("digits")) {
      return NumberFactory.getNumberForTag(tag);
    }

    return switch (tag.toLowerCase(Locale.ROOT)) {
      case "guid" -> String.valueOf(UUID.randomUUID());
      case "today", "tomorrow", "yesterday" -> String.valueOf(DateFactory.getDateForTag(tag));
      default -> tag;
    };
  }
}
