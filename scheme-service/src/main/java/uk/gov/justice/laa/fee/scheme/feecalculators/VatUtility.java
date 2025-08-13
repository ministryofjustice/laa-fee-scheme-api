package uk.gov.justice.laa.fee.scheme.feecalculators;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculate and add VAT onto provided value.
 */
public final class VatUtility {

  private VatUtility() {
  }

  private static final BigDecimal VatValueRate = new BigDecimal(20);

  /**
   * Adds VAT to a given value.
   */
  public static BigDecimal addVat(BigDecimal value) {
    return value
        .add(value.multiply(VatValueRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
        .setScale(2, RoundingMode.HALF_UP);
  }
}