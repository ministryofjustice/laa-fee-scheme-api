package uk.gov.justice.laa.fee.scheme.util;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Utility class for Date string conversions.
 */

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
  public static @NotNull LocalDate toLocalDate(String inputStr) {
    String strDate = inputStr.substring(0, 6); // ddMMyy
    int ufnYear = Integer.parseInt(strDate.substring(4, 6));
    int baseYear = (ufnYear >= PIVOT_YEAR) ? NINETEEN_HUNDRED : TWENTY_HUNDRED;

    return LocalDate.parse(strDate,
        new DateTimeFormatterBuilder()
            .appendPattern("ddMM")
            .appendValueReduced(ChronoField.YEAR, 2, 2, baseYear)
            .toFormatter()
    );
  }
}
