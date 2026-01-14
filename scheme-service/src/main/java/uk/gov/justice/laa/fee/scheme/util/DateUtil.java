package uk.gov.justice.laa.fee.scheme.util;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Utility class for Date string conversions.
 */

public final class DateUtil {

  private DateUtil() {
  }

  /**
   * Converts a Date String value to LocalDate.
   * if UFN year greater than 50, parse as 1900, else 2000
   *
   * @param inputStr the Date String value to convert
   * @return the converted LocalDate
   */
  public static @NotNull LocalDate toLocalDate(String inputStr) {
    String strDate = inputStr.substring(0, 6); // ddMMyy

    int ufnYear = Integer.parseInt(strDate.substring(4, 6));
    int pivot = 50;
    int baseYear = (ufnYear >= pivot) ? 1900 : 2000;

    // Define formatter for ddMMyy
    DateTimeFormatter formatter =
        new DateTimeFormatterBuilder()
            .appendPattern("ddMM")
            .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear)
            .toFormatter();
    // Parse into LocalDate
    return LocalDate.parse(strDate, formatter);
  }

}
