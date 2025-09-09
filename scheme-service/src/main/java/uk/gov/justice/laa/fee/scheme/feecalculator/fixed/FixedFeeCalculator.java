package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
public final class FixedFeeCalculator {
  private FixedFeeCalculator() {
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    return FeeCalculationUtility.calculate(feeEntity, feeCalculationRequest);
  }
}
