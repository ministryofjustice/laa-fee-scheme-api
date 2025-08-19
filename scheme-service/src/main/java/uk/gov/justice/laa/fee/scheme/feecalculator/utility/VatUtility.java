package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Calculate and add VAT onto provided value.
 */
public final class VatUtility {

  private VatUtility() {
  }

  private static final NavigableMap<LocalDate, BigDecimal> VAT_RATES = new TreeMap<>();

  static {
    VAT_RATES.put(LocalDate.of(1991, 3, 19), BigDecimal.valueOf(17.5));
    VAT_RATES.put(LocalDate.of(2008, 12, 1), BigDecimal.valueOf(15));
    VAT_RATES.put(LocalDate.of(2010, 1, 1), BigDecimal.valueOf(17.5));
    VAT_RATES.put(LocalDate.of(2011, 1, 4), BigDecimal.valueOf(20));
  }

  /**
   * Adds VAT to a value.
   */
  public static BigDecimal addVat(BigDecimal value, LocalDate startDate) {
    BigDecimal rate = getVatRateForDate(startDate);
    return addVatUsingRate(value, rate);
  }

  /**
   * Retrieves the VAT rate for the date.
   */
  public static BigDecimal getVatRateForDate(LocalDate startDate) {
    return VAT_RATES.floorEntry(startDate).getValue();
  }

  /**
   * Adds VAT to a given value using the tax rate.
   */
  public static BigDecimal addVatUsingRate(BigDecimal value, BigDecimal taxRate) {
    return value
        .add(value.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
        .setScale(2, RoundingMode.HALF_UP);
  }
}