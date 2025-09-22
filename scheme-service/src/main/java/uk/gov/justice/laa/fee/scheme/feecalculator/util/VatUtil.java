package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.NavigableMap;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Calculate and add VAT onto provided value.
 */
@Slf4j
public final class VatUtil {

  private VatUtil() {
  }

  private static final NavigableMap<LocalDate, BigDecimal> VAT_RATES = new TreeMap<>();

  static {
    VAT_RATES.put(LocalDate.of(1991, 3, 19), BigDecimal.valueOf(17.5));
    VAT_RATES.put(LocalDate.of(2008, 12, 1), BigDecimal.valueOf(15));
    VAT_RATES.put(LocalDate.of(2010, 1, 1), BigDecimal.valueOf(17.5));
    VAT_RATES.put(LocalDate.of(2011, 1, 4), BigDecimal.valueOf(20));
  }

  /**
   * Get VAT amount for a given value.
   */
  public static BigDecimal getVatAmount(BigDecimal value, LocalDate startDate, boolean vatIndicator) {
    if (!vatIndicator) {
      log.info("VAT is not applicable for fee calculation");
      return BigDecimal.ZERO;
    }

    log.info("Calculate VAT for fee calculation");
    BigDecimal rate = getVatRateForDate(startDate);
    return calculateVatAmount(value, rate);
  }

  /**
   * Retrieves the VAT rate for the date.
   */
  public static BigDecimal getVatRateForDate(LocalDate startDate) {
    BigDecimal vatRate = VAT_RATES.floorEntry(startDate).getValue();

    log.info("Retrieved VAT Rate: {}", vatRate);

    return vatRate;
  }

  /**
   * Get VAT amount to a given value using the tax rate.
   */
  public static BigDecimal calculateVatAmount(BigDecimal value, BigDecimal taxRate) {
    return value.multiply(taxRate)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        .setScale(2, RoundingMode.HALF_UP);
  }
}