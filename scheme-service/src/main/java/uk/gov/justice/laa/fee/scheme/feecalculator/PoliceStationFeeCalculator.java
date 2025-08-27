package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.calculate;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
public final class PoliceStationFeeCalculator {

  public static final String INVC = "INVC";

  private PoliceStationFeeCalculator() {
  }

  /**
   * Determines the calculation based on police fee code.
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, PoliceStationFeesEntity policeStationFeesEntity,
                                              FeeCalculationRequest feeData) {

    if (policeStationFeesEntity == null) {
      throw new PoliceStationFeeNotFoundException(feeEntity.getFeeCode(), feeData.getPoliceStationSchemeId());
    }
    String policeStationFeeCode = feeData.getFeeCode();

    if (policeStationFeeCode.equals(INVC)) {
      return calculateFeesUsingPoliceStation(policeStationFeesEntity, feeData);
    } else {
      return calculateFeesUsingFeeCode(feeEntity, feeData);
    }
  }

  /**
   * Gets fixed fee from police station fees.
   */
  private static FeeCalculationResponse calculateFeesUsingPoliceStation(PoliceStationFeesEntity policeStationFeesEntity,
                                                                            FeeCalculationRequest feeData) {
    BigDecimal baseFee = policeStationFeesEntity.getFixedFee();

    return calculate(baseFee, feeData);
  }

  /**
   * Gets fixed fee from static fixed_fee.
   */
  private static FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeData) {

    BigDecimal baseFee = feeEntity.getProfitCostLimit();

    return calculate(baseFee, feeData);
  }
}