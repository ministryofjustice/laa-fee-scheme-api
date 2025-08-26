package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.calculate;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.exception.InvalidMediationSessionException;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility;
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
    if (feeEntity.getFixedFee() == null) {
      // Where fee code type is MED numberOfMediationSessions is required, numberOfMediationSessions will determine fixed fee amount.
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
    Integer numberOfMediationSessions = feeData.getNumberOfMediationSessions();
    if (numberOfMediationSessions == null || numberOfMediationSessions <= 0) {
      throw new InvalidMediationSessionException(feeEntity.getFeeCode());
    }

    BigDecimal baseFee = (numberOfMediationSessions == 1) ? feeEntity.getMediationSessionOne() : feeEntity.getMediationSessionTwo();

    return calculate(baseFee, feeData);
  }

  /**
   * Gets fixed fee from static fixed_fee.
   */
  private static FeeCalculationResponse getCalculationWithoutMediationSessions(FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeData) {
    return FeeCalculationUtility.calculate(feeEntity, feeData);
  }
}