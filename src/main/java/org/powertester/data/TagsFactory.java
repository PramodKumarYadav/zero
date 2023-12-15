package org.powertester.data;

import static org.powertester.utils.DateUtils.getDateForTag;

import java.util.Locale;
import java.util.UUID;

public class TagsFactory {
  private TagsFactory() {
    throw new IllegalStateException("Utility class");
  }

  public static String getValueFor(String tag) {
    switch (tag.toLowerCase(Locale.ROOT)) {
      case "guid":
        return String.valueOf(UUID.randomUUID());
      case "digits":
        //                return NumberUtils.getRandomNumber(tag);
        return "123";
      case "today", "tomorrow", "yesterday":
        return String.valueOf(getDateForTag(tag));
      default:
        return tag;
    }
  }
}
