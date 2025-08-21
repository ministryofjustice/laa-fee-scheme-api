package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
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
   * Fixed fee + netDisbursementAmount = subtotal.
   * If Applicable add VAT to fixed fee,
   * fixedFeeWithVat + netDisbursementAmount + netDisbursementVatAmount = finalTotal.
   */
  public static FeeCalculationResponse buildFixedFeeResponse(BigDecimal fixedFee, FeeCalculationRequest feeCalculationRequest) {
    boolean vatApplicable = Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator());

    BigDecimal fixedFeeVatAmount = VatUtility.getVatValue(fixedFee, feeCalculationRequest.getStartDate(), vatApplicable);

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal netDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal finalTotalWithoutVat = fixedFee
        .add(netDisbursementAmount)
        .add(netDisbursementVatAmount);

    BigDecimal finalTotalWithVat = BigDecimal.ZERO;
    if (vatApplicable) {
      finalTotalWithVat = finalTotalWithoutVat
          .add(fixedFeeVatAmount);
    }

    return new FeeCalculationResponse()
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculationItems(FeeCalculation.builder()
            .calculatedClaimAmount(toDouble(vatApplicable ? finalTotalWithVat : finalTotalWithoutVat))
            .subTotal(toDouble(finalTotalWithoutVat.subtract(netDisbursementVatAmount)))
            .build());
  }

}
