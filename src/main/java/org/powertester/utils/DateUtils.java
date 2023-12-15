package org.powertester.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DateUtils {

  private DateUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final Map<String, Supplier<LocalDate>> DATE_TAGS;

  static {
    DATE_TAGS = new HashMap<>();
    DATE_TAGS.put("today", LocalDate::now);
    DATE_TAGS.put("tomorrow", () -> LocalDate.now().plus(1, ChronoUnit.DAYS));
    DATE_TAGS.put("yesterday", () -> LocalDate.now().minus(1, ChronoUnit.DAYS));
    // Add more tags as needed
  }

  public static LocalDate getDateForTag(String tag) {
    Supplier<LocalDate> dateSupplier = DATE_TAGS.get(tag.toLowerCase());
    if (dateSupplier != null) {
      return dateSupplier.get();
    } else {
      // Handle custom date or invalid tag
      try {
        return LocalDate.parse(tag, DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid date tag or format: " + tag);
      }
    }
  }

  public static void main(String[] args) {
    System.out.println("Today: " + getDateForTag("today"));
    System.out.println("Tomorrow: " + getDateForTag("tomorrow"));
    System.out.println("Yesterday: " + getDateForTag("yesterday"));
    System.out.println("Custom Date (2023-12-15): " + getDateForTag("2023-12-15"));
  }

  public static String getDateAsString(String format) {
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return now.format(formatter);
  }
}
