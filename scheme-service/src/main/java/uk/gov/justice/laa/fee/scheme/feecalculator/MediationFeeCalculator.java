package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.buildFixedFeeResponse;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 */
public final class MediationFeeCalculator {

  private MediationFeeCalculator() {
  }

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  public static FeeCalculationResponse getFee(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    Integer numberOfMediationSessions = feeCalculationRequest.getNumberOfMediationSessions();

    if (numberOfMediationSessions == null) {
      return getCalculationWithoutMediationSessions(feeEntity, feeCalculationRequest);
    } else {
      return getCalculationWithMediationSessions(feeEntity, feeCalculationRequest);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse getCalculationWithMediationSessions(FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeCalculationRequest) {
    BigDecimal baseFee = BigDecimal.ZERO;

    if (feeCalculationRequest.getNumberOfMediationSessions() == 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionOne().doubleValue());
    } else if (feeCalculationRequest.getNumberOfMediationSessions() > 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionTwo().doubleValue());
    }
    return buildFixedFeeResponse(baseFee, feeCalculationRequest);
  }

  /**
   * Gets fixed fee from static fixed_fee.
   */
  private static FeeCalculationResponse getCalculationWithoutMediationSessions(FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeCalculationRequest) {
    BigDecimal baseFee = feeEntity.getFixedFee();

    return buildFixedFeeResponse(baseFee, feeCalculationRequest);
  }
}