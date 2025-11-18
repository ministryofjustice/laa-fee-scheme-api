package uk.gov.justice.laa.fee.scheme.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.repository.VatRatesRepository;

/**
 * Service for retrieving VAT rates.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VatRatesService {

  private final VatRatesRepository vatRatesRepository;

  /**
   * Returns the VAT rate for a given date and VAT indicator.
   *
   * @param date         the date to apply the VAT
   * @param vatIndicator indicates whether VAT is applicable, if true returns VAT rate otherwise BigDecimal.ZERO
   * @return the VAT rate
   */
  public BigDecimal getVatRateForDate(LocalDate date, Boolean vatIndicator) {

    if (!Boolean.TRUE.equals(vatIndicator)) {
      log.info("VAT is not applicable for fee calculation");
      return BigDecimal.ZERO;
    }

    VatRatesEntity vatRatesEntity = vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date);
    BigDecimal vatRate = vatRatesEntity.getVatRate();

    log.info("Retrieved VAT Rate: {}", vatRatesEntity.getVatRate());
    return vatRate;
  }
}
