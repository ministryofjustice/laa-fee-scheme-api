package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.enums.ValidationError.ERRMED1;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class MediationFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(MEDIATION);
  }

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Mediation fixed fee");

    if (feeEntity.getFixedFee() == null) {
      log.info("Using numberOfMediationSessions to calculate Mediation fee");
      // Where fee code type is MED numberOfMediationSessions is required, numberOfMediationSessions will determine fixed fee amount.
      return getCalculationWithMediationSessions(feeEntity, feeCalculationRequest);
    } else {
      log.info("Using fixed fee to calculate Mediation fee");
      // Where fee code type is MAM numberOfMediationSessions is not required, and will be omitted from calculation
      return calculateMediation(feeEntity.getFixedFee(), feeCalculationRequest, feeEntity);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse getCalculationWithMediationSessions(FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeCalculationRequest) {
    log.info("Check numberOfMediationSessions is valid");
    Integer numberOfMediationSessions = feeCalculationRequest.getNumberOfMediationSessions();
    if (numberOfMediationSessions == null || numberOfMediationSessions <= 0) {
      log.info("umberOfMediationSessions is invalid");
      throw new ValidationException(ERRMED1, new FeeContext(feeCalculationRequest));
    }

    BigDecimal baseFee = (numberOfMediationSessions == 1) ? feeEntity.getMediationFeeLower() : feeEntity.getMediationFeeHigher();

    return calculateMediation(baseFee, feeCalculationRequest, feeEntity);
  }

  private static FeeCalculationResponse calculateMediation(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest,
                                                           FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, and VAT where applicable");
    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(false)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFee))
            .build())
        .build();
  }

}