package uk.gov.justice.laa.fee.scheme.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.repository.VatRatesRepository;
import uk.gov.justice.laa.fee.scheme.service.model.VatResult;

/**
 * Service for calculating VAT.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VatService {

  private final VatRatesRepository vatRatesRepository;

  /**
   * Calculate VAT amount for a given value, start date and vatIndicator.
   */
  public VatResult calculateVat(BigDecimal value, LocalDate startDate, Boolean vatIndicator) {
    if (!Boolean.TRUE.equals(vatIndicator)) {
      log.info("VAT is not applicable for fee calculation");
      return new VatResult(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    BigDecimal vatRate = getVatRateForDate(startDate);

    BigDecimal vatAmount = calculateVatAmount(value, vatRate);

    return new VatResult(vatAmount, vatRate);
  }

  /**
   * Get VAT amount to a given value using the tax rate.
   */
  private BigDecimal calculateVatAmount(BigDecimal value, BigDecimal taxRate) {
    log.info("Calculate VAT amount");

    return value.multiply(taxRate)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Retrieves the VAT rate for the date.
   */
  private BigDecimal getVatRateForDate(LocalDate startDate) {

    VatRatesEntity vatRatesEntity = vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(startDate);
    BigDecimal vatRate =  vatRatesEntity.getVatRate();

    log.info("Retrieved VAT Rate: {}", vatRatesEntity.getVatRate());
    return vatRate;
  }
}
