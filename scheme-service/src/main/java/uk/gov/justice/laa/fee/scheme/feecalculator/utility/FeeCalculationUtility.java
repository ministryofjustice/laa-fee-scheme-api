package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.addVat;

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
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee,
   * baWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(BigDecimal fixedFee,
                                                             FeeCalculationRequest feeCalculationRequest) {
    return buildResponse(fixedFee, BigDecimal.ZERO, feeCalculationRequest);
  }

  /**
   * Fixed fee + bolt ons (if exist) + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee + bolt ons,
   * fixedFeeWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(FeeEntity feeEntity,
                                                             FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    return buildResponse(fixedFee, boltOnValue, feeCalculationRequest);
  }

  private static FeeCalculationResponse buildResponse(BigDecimal fixedFee, BigDecimal boltOnValue,
                                                      FeeCalculationRequest feeCalculationRequest) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal fixedFeeWithBoltOns = fixedFee.add(boltOnValue);

    BigDecimal subTotal = fixedFeeWithBoltOns.add(netDisbursementAmount);

    BigDecimal finalTotal = addVat(fixedFeeWithBoltOns, feeCalculationRequest.getStartDate(),
        feeCalculationRequest.getVatIndicator())
        .add(netDisbursementAmount).add(disbursementVatAmount);

    return new FeeCalculationResponse()
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal(toDouble(subTotal))
            .totalAmount(toDouble(finalTotal))
            .build());
  }

}
