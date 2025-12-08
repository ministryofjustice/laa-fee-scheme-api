package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.ErrorType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MediationFixedFeeCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Mediation fixed fee");

    if (feeEntity.getFixedFee() == null) {
      log.info("Using numberOfMediationSessions to calculate Mediation fee");
      // Where fee code type is not ASSA, ASSS, ASST numberOfMediationSessions is required,
      // numberOfMediationSessions will determine fixed fee amount.
      return getCalculationWithMediationSessions(feeEntity, feeCalculationRequest);
    } else {
      log.info("Using fixed fee to calculate Mediation fee");
      // Where fee code type is ASSA, ASSS, ASST  numberOfMediationSessions is not required, and will be omitted from calculation
      return calculateMediation(feeEntity.getFixedFee(), feeCalculationRequest, feeEntity);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private FeeCalculationResponse getCalculationWithMediationSessions(FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeCalculationRequest) {
    log.info("Check numberOfMediationSessions is valid");
    Integer numberOfMediationSessions = feeCalculationRequest.getNumberOfMediationSessions();
    if (numberOfMediationSessions == null || numberOfMediationSessions <= 0) {
      log.info("numberOfMediationSessions is invalid");
      throw new ValidationException(ErrorType.ERR_MEDIATION_SESSIONS, new FeeContext(feeCalculationRequest));
    }

    BigDecimal baseFee = (numberOfMediationSessions == 1) ? feeEntity.getMediationFeeLower() : feeEntity.getMediationFeeHigher();

    return calculateMediation(baseFee, feeCalculationRequest, feeEntity);
  }

  private FeeCalculationResponse calculateMediation(BigDecimal fixedFeeAmount, FeeCalculationRequest feeCalculationRequest,
                                                           FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");

    // Calculate VAT if applicable
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatIndicator)
        .vatRateApplied(toDoubleOrNull(vatRate))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation);
  }
}