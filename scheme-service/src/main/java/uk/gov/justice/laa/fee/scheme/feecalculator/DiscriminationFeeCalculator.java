package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.addVatIfApplicable;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
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

  private static final String WARNING_CODE = "123"; // clarify what code should be
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
    if (feeTotal.compareTo(escapeThresholdLimit) > 0) {
      warning = Warning.builder()
          .warrningCode(WARNING_CODE)
          .warningDescription(WARNING_CODE_DESCRIPTION)
          .build();
      feeTotal = escapeThresholdLimit;
    }

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Add net disbursement amount to get subtotal
    BigDecimal subTotalWithoutTax = feeTotal.add(netDisbursementAmount);

    // Add VAT if applicable to subtotal and add disbursement amounts to get final total
    BigDecimal finalTotal = addVatIfApplicable(feeTotal, feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator())
        .add(netDisbursementAmount).add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .warning(warning)
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal((toDouble(subTotalWithoutTax)))
            .totalAmount((toDouble(finalTotal)))
            .build())
        .build();
  }
}
