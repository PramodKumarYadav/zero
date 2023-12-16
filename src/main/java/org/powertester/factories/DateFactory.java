package org.powertester.factories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateFactory {

  private DateFactory() {
    throw new IllegalStateException("Utility class");
  }

  private static final Map<String, Supplier<LocalDate>> DATE_TAGS;

  static {
    DATE_TAGS = new HashMap<>();
    DATE_TAGS.put("today", LocalDate::now);
    DATE_TAGS.put("tomorrow", () -> LocalDate.now().plusDays(1));
    DATE_TAGS.put("yesterday", () -> LocalDate.now().minusDays(1));
    // Add more tags as needed
  }

  public static LocalDate getDateForTag(String tag) {
    Supplier<LocalDate> dateSupplier = DATE_TAGS.get(tag.toLowerCase());
    log.debug("Date supplier for tag '{}' is: {}", tag, dateSupplier.get());
    if (dateSupplier.get() != null) {
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
    log.debug("Today: " + getDateForTag("today"));
    log.debug("Tomorrow: " + getDateForTag("tomorrow"));
    log.debug("Yesterday: " + getDateForTag("yesterday"));
    log.debug("Custom Date (2023-12-15): " + getDateForTag("2023-12-15"));
  }

  public static String getDateAsString(String format) {
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return now.format(formatter);
  }
}
