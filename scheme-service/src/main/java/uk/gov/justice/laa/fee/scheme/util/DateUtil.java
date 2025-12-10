package uk.gov.justice.laa.fee.scheme.util;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for Date string conversions.
 */

public final class DateUtil {

  private static final String DATE_FORMAT = "ddMMyy";

  private DateUtil() {
  }

  /**
   * Converts a Date String value to LocalDate.
   *
   * @param inputStr the Date String value to convert
   * @return the converted LocalDate
   */
  @NotNull
  public static @NotNull LocalDate toLocalDate(String inputStr) {
    String strDate = inputStr.substring(0, 6); // "120325"
    // Define formatter for ddMMyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    // Parse into LocalDate
    return LocalDate.parse(strDate, formatter);
  }

}
