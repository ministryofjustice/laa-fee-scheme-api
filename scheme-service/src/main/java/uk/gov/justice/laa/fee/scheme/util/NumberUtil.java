package uk.gov.justice.laa.fee.scheme.util;

import java.math.BigDecimal;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utility class for number conversions.
 */
public final class NumberUtil {

  private NumberUtil() {
  }

  /**
   * Converts a Number value to a BigDecimal with scaling.
   *
   * @param value the Number value to convert
   * @return the converted BigDecimal
   */
  public static BigDecimal toBigDecimal(Number value) {
    return value == null
        ? BigDecimal.ZERO
        : NumberUtils.toScaledBigDecimal(new BigDecimal(value.toString()));
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
   * Converts a Double value to a BigDecimal with scaling.
   * Where null or Zero, return null.
   */
  public static Double toDoubleOrNull(BigDecimal value) {
    if (value == null || BigDecimal.ZERO.compareTo(value) == 0) {
      return null;
    }
    return value.doubleValue();
  }

  /**
   * Returns the given BigDecimal value or BigDecimal.ZERO if the value is null.
   *
   * @param value the BigDecimal value to check
   * @return the original value or BigDecimal.ZERO if null
   */
  public static BigDecimal defaultToZeroIfNull(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
  }

}
