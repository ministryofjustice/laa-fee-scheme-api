package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.buildFixedFeeResponse;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.exception.InvalidMediationSessionException;
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
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeData) {
    Integer numberOfMediationSessions = feeData.getNumberOfMediationSessions();

    if (feeEntity.getFixedFee() == null) {
      // Where fee code type is MED numberOfMediationSessions required, numberOfMediationSessions will determine fixed fee amount.
      if (numberOfMediationSessions == null || numberOfMediationSessions <= 0) {
        throw new InvalidMediationSessionException(feeEntity.getFeeCode());
      }
      return getCalculationWithMediationSessions(feeEntity, feeData);
    } else {
      // Where fee code type is MAM numberOfMediationSessions is not required, and will be omitted from calculation
      return getCalculationWithoutMediationSessions(feeEntity, feeData);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse getCalculationWithMediationSessions(FeeEntity feeEntity,
                                                                            FeeCalculationRequest feeData) {
    BigDecimal baseFee = BigDecimal.ZERO;

    if (feeData.getNumberOfMediationSessions() == 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionOne().doubleValue());
    } else if (feeData.getNumberOfMediationSessions() > 1) {
      baseFee = BigDecimal.valueOf(feeEntity.getMediationSessionTwo().doubleValue());
    }
    return buildFixedFeeResponse(baseFee, feeData);
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