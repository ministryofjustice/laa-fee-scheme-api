package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.buildFixedFeeResponse;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the mediation fee for a given fee entity and fee data.
 */
public final class PoliceStationFeeCalculator {

  public static final String INVC = "INVC";

  private PoliceStationFeeCalculator() {
  }

  /**
   * Determines whether the calculation should include mediation sessions based presence of numberOfMediationSessions.
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, PoliceStationFeesEntity policeStationFeesEntity,
                                              FeeCalculationRequest feeData) {
    String policeStationFeeCode = feeData.getFeeCode();

    if (policeStationFeeCode.equals(INVC)) {
      return calculateFeesUsingPoliceStation(policeStationFeesEntity, feeData);
    } else {
      return calculateFeesUsingFeeCode(feeEntity, feeData);
    }
  }

  /**
   * Gets fixed fee depending on number if mediation sessions.
   */
  private static FeeCalculationResponse calculateFeesUsingPoliceStation(PoliceStationFeesEntity policeStationFeesEntity,
                                                                            FeeCalculationRequest feeData) {
    BigDecimal baseFee = policeStationFeesEntity.getFixedFee();

    return buildFixedFeeResponse(baseFee, feeData);
  }

  /**
   * Gets fixed fee from static fixed_fee.
   */
  private static FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeCalculationRequest) {

    BigDecimal baseFee = feeEntity.getProfitCostLimit();

    return buildFixedFeeResponse(baseFee, feeCalculationRequest);
  }
}