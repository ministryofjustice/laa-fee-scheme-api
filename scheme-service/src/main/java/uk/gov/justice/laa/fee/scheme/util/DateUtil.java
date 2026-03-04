package uk.gov.justice.laa.fee.scheme.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for Date string conversions.
 */
@Slf4j
public final class DateUtil {
  private static final int PIVOT_YEAR = 50;
  private static final int NINETEEN_HUNDRED = 1900;
  private static final int TWENTY_HUNDRED = 2000;

  private DateUtil() {}

  /**
   * Converts a date string in the format ddMMyy.
   * Century is determined by the year value:
   * - 50-99 = 1900s
   * - 00-49 = 2000s
   *
   * @param inputStr the Date String value to convert
   * @return the converted LocalDate
   */
  public static LocalDate toLocalDate(String inputStr) {
    if (inputStr == null) {
      return null;
    }

    if (inputStr.length() < 6) {
      throw new IllegalArgumentException("Date string length less than 6 characters: " + inputStr);
    }

    String strDate = inputStr.substring(0, 6); // ddMMyy
    int ufnYear = Integer.parseInt(strDate.substring(4, 6));
    int baseYear = (ufnYear >= PIVOT_YEAR) ? NINETEEN_HUNDRED : TWENTY_HUNDRED;

    return LocalDate.parse(strDate,
        new DateTimeFormatterBuilder()
            .appendPattern("ddMM")
            .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear)
            .toFormatter());
  }
}

