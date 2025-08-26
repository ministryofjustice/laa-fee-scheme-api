package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.defaultToZeroIfNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatValue;

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
  public static FeeCalculationResponse calculate(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    return calculateAndBuildResponse(fixedFee, boltOnValue, feeCalculationRequest, feeEntity);
  }

  /**
   * Given fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to subtotal.
   * subtotalWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse calculate(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    return calculateAndBuildResponse(fixedFee, null, feeCalculationRequest, feeEntity);
  }

  private static FeeCalculationResponse calculateAndBuildResponse(BigDecimal fixedFee, BigDecimal boltOnValue,
                                                                  FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    boolean vatApplicable = Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator());
    BigDecimal boltOnVatValue = BigDecimal.ZERO;
    if (boltOnValue != null) {
      boltOnVatValue = getVatValue(boltOnValue, feeCalculationRequest.getStartDate(), vatApplicable);
    }
    BigDecimal fixedFeeVatValue = getVatValue(fixedFee, feeCalculationRequest.getStartDate(), vatApplicable);

    BigDecimal calculatedVatValue = boltOnVatValue.add(fixedFeeVatValue);

    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatValue)
        .add(boltOnValue != null ? boltOnValue : BigDecimal.ZERO)
        .add(boltOnVatValue)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId("temp hardcoded till clarification")
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(feeCalculationRequest.getStartDate())))
            .calculatedVatAmount(toDouble(calculatedVatValue))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFee)).build())
        .build();
  }

}
