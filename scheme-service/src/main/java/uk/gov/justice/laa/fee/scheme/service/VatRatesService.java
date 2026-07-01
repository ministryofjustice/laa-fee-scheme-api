package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getCaseConcludedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.VatRatesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
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

    if (BooleanUtils.isNotTrue(vatIndicator)) {
      log.info("VAT is not applicable for fee calculation");
      return BigDecimal.ZERO;
    }

    VatRatesEntity vatRatesEntity = vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date);
    BigDecimal vatRate = vatRatesEntity.getVatRate();

    log.info("Retrieved VAT Rate: {}", vatRatesEntity.getVatRate());
    return vatRate;
  }

  /**
   * Returns the VAT rate applicable for a given date, regardless of the VAT indicator.
   *
   * @param date the date to apply the VAT
   * @return the VAT rate
   */
  public BigDecimal getVatRateForDate(LocalDate date) {
    VatRatesEntity vatRatesEntity = vatRatesRepository.findTopByStartDateLessThanEqualOrderByStartDateDesc(date);
    log.info("Retrieved VAT Rate: {}", vatRatesEntity.getVatRate());
    return vatRatesEntity.getVatRate();
  }

  /**
   * Returns the VAT rate derived from the fee calculation request,
   * resolving the case concluded date and VAT indicator from the request.
   *
   * @param feeCalculationRequest the fee calculation request
   * @return the VAT rate
   */
  public BigDecimal getVatRateForRequest(FeeCalculationRequest feeCalculationRequest) {
    LocalDate caseConcludedDate = getCaseConcludedDate(feeCalculationRequest);
    return getVatRateForDate(caseConcludedDate, feeCalculationRequest.getVatIndicator());
  }

  /**
   * Returns the VAT rate derived from the fee calculation request,
   * resolving the case concluded date and VAT indicator from the request.
   *
   * @param feeCalculationRequest the fee calculation request
   * @param vatIndicator          indicates whether VAT is applicable, if true returns VAT rate otherwise BigDecimal.ZERO
   * @return the VAT rate
   */
  public BigDecimal getVatRateForRequest(FeeCalculationRequest feeCalculationRequest, Boolean vatIndicator) {
    LocalDate caseConcludedDate = getCaseConcludedDate(feeCalculationRequest);
    return getVatRateForDate(caseConcludedDate, vatIndicator);
  }


}
