package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for Date string conversions.
 */

public final class DateUtility {

  public static final String DD_MM_MYY = "ddMMyy";

  private DateUtility() {
  }

  /**
   * Converts a Date String value to LocalDate.
   *
   * @param inputStr the Date String value to convert
   * @return the converted LocalDate
   */
  public static LocalDate toLocalDate(String inputStr) throws DateTimeParseException {
    LocalDate localDate;

    String strDate = inputStr.substring(0, 6); // "120325"
    // Define formatter for ddMMyy
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_MYY);
    // Parse into LocalDate
    localDate = LocalDate.parse(strDate, formatter);
    return localDate;
  }

}
