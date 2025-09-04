package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatRateForDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the police station fee for a given fee entity and fee data.
 */
public final class PoliceStationFixedFeeCalculator {

  private static final String INVC = "INVC";

  private static final String INVB1 = "INVB1";

  private static final String INVB2 = "INVB2";

  private static final String WARNING_NET_PROFIT_COSTS = "warning net profit costs";

  private PoliceStationFixedFeeCalculator() {
  }

  /**
   * Determines the calculation based on police fee code.
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, PoliceStationFeesEntity policeStationFeesEntity,
                                              FeeCalculationRequest feeData) {

    if (feeData.getFeeCode().equals(INVC)) {
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

    return calculateAndBuildResponsePoliceStation(baseFee, feeData, policeStationFeesEntity);
  }

  /**
   * Gets fixed fee from static fixed_fee.
   */
  private static FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                                               FeeCalculationRequest feeData) {

    BigDecimal baseFee;
    FeeCalculationResponse feeCalculationResponse = null;

    if (feeData.getFeeCode().equals(INVB1) || feeData.getFeeCode().equals(INVB2)) {
      baseFee = feeEntity.getFixedFee();
      feeCalculationResponse = FeeCalculationUtility.calculate(baseFee, feeData, feeEntity);
    }

    return feeCalculationResponse;
  }

  private static FeeCalculationResponse calculateAndBuildResponsePoliceStation(BigDecimal fixedFee,
                                                                               FeeCalculationRequest feeCalculationRequest,
                                                                               PoliceStationFeesEntity policeStationFeesEntity) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Apply VAT where applicable
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = feeCalculationRequest.getStartDate();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, startDate, vatApplicable);

    BigDecimal finalTotal = fixedFee
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(policeStationFeesEntity.getFeeSchemeCode())
        .claimId("temp hardcoded till clarification")
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee)).build())
        .build();
  }

}