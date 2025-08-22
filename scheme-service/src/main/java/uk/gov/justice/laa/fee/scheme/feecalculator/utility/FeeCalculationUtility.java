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
   * fixedFeeWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(FeeEntity feeEntity,
                                                             FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    BigDecimal fixedFeeWithBoltOns = fixedFee.add(boltOnValue);
    return buildResponse(fixedFeeWithBoltOns, feeCalculationRequest);
  }

  /**
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee,
   * fixed fee + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(BigDecimal fixedFee,
                                                             FeeCalculationRequest feeCalculationRequest) {
    return buildResponse(fixedFee, feeCalculationRequest);
  }

  /**
   * Build a fixed fee response for the given fee code, fixed fee, subtotal and final total.
   */
  public static FeeCalculationResponse buildFixedResponse(String feeCode, BigDecimal subTotal, BigDecimal finalTotal,
                                                          Warning warning) {
    return buildResponse(feeCode, subTotal, finalTotal, warning);
  }

  private static FeeCalculationResponse buildResponse(BigDecimal feeTotal, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Add net disbursement amount to get subtotal
    BigDecimal subTotal = feeTotal.add(netDisbursementAmount);

    // Add VAT if applicable to subtotal and add disbursement amounts to get final total
    BigDecimal finalTotal = addVatIfApplicable(feeTotal, feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator())
        .add(netDisbursementAmount).add(disbursementVatAmount);

    return buildResponse(feeCalculationRequest.getFeeCode(), subTotal, finalTotal);
  }

  private static FeeCalculationResponse buildResponse(String feeCode, BigDecimal subTotal, BigDecimal finalTotal) {
    return buildResponse(feeCode, subTotal, finalTotal, null);
  }

  private static FeeCalculationResponse buildResponse(String feeCode, BigDecimal subTotal, BigDecimal finalTotal,
                                                      Warning warning) {
    return FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .feeCalculation(FeeCalculation.builder()
            .subTotal(toDouble(subTotal))
            .totalAmount(toDouble(finalTotal))
            .build())
        .warning(warning)
        .build();
  }

}
