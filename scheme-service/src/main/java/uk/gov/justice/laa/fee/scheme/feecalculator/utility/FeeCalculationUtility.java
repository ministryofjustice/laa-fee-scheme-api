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
import uk.gov.justice.laa.fee.scheme.model.Warning;

/**
 * Utility class for fee calculation operations.
 */
public final class FeeCalculationUtility {

  private FeeCalculationUtility() {
  }

  /**
   * Fixed fee + bolt ons (if exists) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee + bolt ons,
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(FeeEntity feeEntity,
                                                             FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    BigDecimal fixedFeeWithBoltOns = fixedFee.add(boltOnValue);
    return buildResponse(fixedFeeWithBoltOns, feeCalculationRequest, null);
  }

  /**
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee,
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(BigDecimal fixedFee,
                                                             FeeCalculationRequest feeCalculationRequest) {
    return buildResponse(fixedFee, feeCalculationRequest, null);
  }

  private static FeeCalculationResponse buildResponse(BigDecimal feeTotal, FeeCalculationRequest feeCalculationRequest, Warning warning) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Add net disbursement amount to get subtotal
    BigDecimal subTotal = feeTotal.add(netDisbursementAmount);

    // Add VAT if applicable to subtotal and add disbursement amounts to get final total
    BigDecimal finalTotal = addVatIfApplicable(feeTotal, feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator())
        .add(netDisbursementAmount).add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .warning(warning)
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal(toDouble(subTotal))
            .totalAmount(toDouble(finalTotal)).build()).build();
  }

}
