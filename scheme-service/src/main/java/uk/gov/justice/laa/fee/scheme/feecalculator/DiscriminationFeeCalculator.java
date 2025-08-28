package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.Warning;

/**
 * Calculate the discrimination fee for a given fee entity and fee calculation request.
 */
public final class DiscriminationFeeCalculator {

  private DiscriminationFeeCalculator() {
  }

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeEntity             the fee entity containing fee details
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal travelAndWaitingCosts = toBigDecimal(feeCalculationRequest.getTravelAndWaitingCosts());

    BigDecimal feeTotal = netProfitCosts.add(netCostOfCounsel).add(travelAndWaitingCosts);

    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    // @TODO: escape case logic TBC
    Warning warning = null;
    boolean escaped = false;
    if (feeTotal.compareTo(escapeThresholdLimit) > 0) {
      warning = Warning.builder()
          .warningDescription(WARNING_CODE_DESCRIPTION)
          .build();
      feeTotal = escapeThresholdLimit;
      escaped = true;
    }

    // Apply VAT where applicable
    BigDecimal calculatedVatValue = VatUtility.getVatValue(
        feeTotal,
        feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator());

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal finalTotal = feeTotal
        .add(calculatedVatValue)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId("temp hardcoded till clarification")
        .warning(warning)
        .escapeCaseFlag(escaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(feeCalculationRequest.getVatIndicator())
            .vatRateApplied(toDouble(VatUtility.getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(calculatedVatValue))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .hourlyTotalAmount(toDouble(feeTotal))
            .netCostOfCounselAmount(toDouble(netCostOfCounsel))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .travelAndWaitingCostAmount(toDouble(travelAndWaitingCosts))
            .build())
        .build();
  }
}
