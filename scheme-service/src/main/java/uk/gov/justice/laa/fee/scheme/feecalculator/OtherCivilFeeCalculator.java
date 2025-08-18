package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.buildFixedFeeResponse;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the Other Civil Cases fee for a given fee entity and fee data.
 */
public final class OtherCivilFeeCalculator {

  private OtherCivilFeeCalculator() {
  }

  /**
   * Calculated fee for Other Civil Cases based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = feeEntity.getFixedFee();

    return buildFixedFeeResponse(fixedFee, feeCalculationRequest);
  }
}
