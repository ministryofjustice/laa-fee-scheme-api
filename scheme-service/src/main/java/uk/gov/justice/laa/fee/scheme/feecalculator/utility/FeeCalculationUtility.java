package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;

import java.math.BigDecimal;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Utility class for fee calculation operations.
 * This class provides methods to calculate fees with VAT and build fee calculation responses.
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
    BigDecimal fixedFeeWithVat = Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator())
        ? VatUtility.addVat(fixedFee, feeCalculationRequest.getStartDate())
        : fixedFee;

    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal netDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal subTotal = fixedFee.add(netDisbursementAmount);

    BigDecimal finalTotal = fixedFeeWithVat.add(netDisbursementAmount).add(netDisbursementVatAmount);

    return new FeeCalculationResponse()
        .feeCode(feeCalculationRequest.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal(toDouble(subTotal))
            .totalAmount(toDouble(finalTotal))
            .build());
  }

}
