package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utility class for number conversions.
 */
public final class NumberUtility {

  private NumberUtility() {
  }

  /**
   * Converts a Double value to a BigDecimal with scaling.
   *
   * @param value the Double value to convert
   * @return the converted BigDecimal
   */
  public static BigDecimal toBigDecimal(Double value) {
    return NumberUtils.toScaledBigDecimal(value);
  }

  /**
   * Converts a Double value to a BigDecimal with scaling.
   *
   * @param value the Double value to convert
   * @return the converted BigDecimal
   */
  public static Double toDouble(BigDecimal value) {
    return NumberUtils.toDouble(value);
  }

  /**
   * Converts a BigDecimal value to a BigDecimal with scaling, defaulting to zero if null.
   *
   * @param value the BigDecimal value to convert
   * @return the converted BigDecimal, or zero if the input is null
   */
  public static BigDecimal defaultToZeroIfNull(BigDecimal value) {
    return NumberUtils.toScaledBigDecimal(value);
  }

}
