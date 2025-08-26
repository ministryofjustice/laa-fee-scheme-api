package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.addVatIfApplicable;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Utility class for fee calculation operations.
 */
public final class FeeCalculationUtility {

  private FeeCalculationUtility() {
  }

  /**
   * Fixed fee + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculate(FeeEntity feeEntity,
                                                 FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    BigDecimal fixedFeeWithBoltOns = fixedFee.add(boltOnValue);
    return calculateAndBuildResponse(fixedFeeWithBoltOns, feeCalculationRequest);
  }

  /**
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculate(BigDecimal fixedFee,
                                                 FeeCalculationRequest feeCalculationRequest) {
    return calculateAndBuildResponse(fixedFee, feeCalculationRequest);
  }

  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal feeTotal, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Add net disbursement amount to get subtotal
    BigDecimal subTotal = feeTotal.add(netDisbursementAmount);

    // Add VAT if applicable to subtotal and add disbursement amounts to get final total
    BigDecimal finalTotal = addVatIfApplicable(feeTotal, feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator())
        .add(netDisbursementAmount).add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal(toDouble(subTotal))
            .totalAmount(toDouble(finalTotal)).build()).build();
  }

}
