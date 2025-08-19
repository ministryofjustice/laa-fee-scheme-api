package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.Warning;

/**
 * TO DO
 * */
public final class ImmigrationAndAsylumFeeCalculator {

  private static final List<String> FEE_CODES_WITH_NO_DISBURSEMENT = List.of("IDAS1", "IDAS2");
  private static final String WARNING_CODE = "123"; // clarify what code should be
  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be
  /**
   * TO DO
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeData) {
    // get the requested disbursement amount from feeCalculationRequest
    BigDecimal requestedNetDisbursementAmount = feeData.getNetDisbursementAmount() != null
        ? BigDecimal.valueOf(feeData.getNetDisbursementAmount())
        : BigDecimal.ZERO;

    // get the requested disbursement VAT amount from feeCalculationRequest
    BigDecimal disbursementVatAmount = feeData.getDisbursementVatAmount() != null
        ? BigDecimal.valueOf(feeData.getDisbursementVatAmount())
        : BigDecimal.ZERO;

    // get the requested detentionAndTravelCosts amount from feeCalculationRequest
    BigDecimal detentionAndTravelCosts = feeData.getDetentionAndWaitingCosts() != null
        ? BigDecimal.valueOf(feeData.getDetentionAndWaitingCosts())
        : BigDecimal.ZERO;

    // get the requested jRFormFilling amount from feeCalculationRequest
    BigDecimal jRFormFilling = feeData.getJrFormFilling() != null
        ? BigDecimal.valueOf(feeData.getJrFormFilling())
        : BigDecimal.ZERO;

    // get the total bolt on value amount from utility class
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeData, feeEntity);

    BigDecimal netDisbursementAmount;
    BigDecimal netDisbursementLimit = feeEntity.getDisbursementLimit();
    Warning warning = null;
    // if fee code is "IDAS1", "IDAS2", and a requestedNetDisbursementAmount exists, return a warning, as these codes
    // are exempt from claiming disbursement
    if (isDisbursementNotAllowed(feeEntity, requestedNetDisbursementAmount)) {
      warning = Warning.builder()
          .warrningCode(WARNING_CODE)
          .warningDescription(WARNING_CODE_DESCRIPTION)
          .build();
      disbursementVatAmount = BigDecimal.ZERO;
      netDisbursementAmount = BigDecimal.ZERO;
    } else {
       netDisbursementAmount = getNetDisbursement(requestedNetDisbursementAmount, netDisbursementLimit, feeData);
    }

    BigDecimal fixedFee = feeEntity.getFixedFee();
    BigDecimal taxableSubTotal = fixedFee.add(boltOnValue).add(detentionAndTravelCosts).add(jRFormFilling);
    taxableSubTotal = Boolean.TRUE.equals(feeData.getVatIndicator())
        ? VatUtility.addVat(taxableSubTotal, feeData.getStartDate())
        : taxableSubTotal;

    BigDecimal subTotalWithoutTax = fixedFee.add(boltOnValue).add(detentionAndTravelCosts).add(jRFormFilling).add(netDisbursementAmount);
    BigDecimal finalTotal = taxableSubTotal.add(netDisbursementAmount).add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .warning(warning)
        .feeCode(feeData.getFeeCode())
        .feeCalculation(FeeCalculation.builder()
            .subTotal(subTotalWithoutTax.setScale(2, RoundingMode.HALF_UP).doubleValue())
            .totalAmount(finalTotal.setScale(2, RoundingMode.HALF_UP).doubleValue())
            .build())
        .build();
  }

  private static BigDecimal getNetDisbursement(BigDecimal requestedNetDisbursementAmount, BigDecimal netDisbursementLimit, FeeCalculationRequest feeData) {

    if (requestedNetDisbursementAmount.compareTo(netDisbursementLimit) <= 0) {
      // Where requestedNetDisbursementAmount is below limit, we allow request as is.
      return  requestedNetDisbursementAmount;
    }
    // Where requestedNetDisbursementAmount is above limit, we allow request as is if they have authorisation
    // if no authorisation default to limit.
    System.out.println("test  " + feeData.getDisbursementPriorAuthority());
    return feeData.getDisbursementPriorAuthority() != null
        ? requestedNetDisbursementAmount
        : netDisbursementLimit;
  }

  private static boolean isDisbursementNotAllowed(FeeEntity feeEntity, BigDecimal requestedAmount) {
    return requestedAmount.compareTo(BigDecimal.ZERO) > 0 && FEE_CODES_WITH_NO_DISBURSEMENT.contains(feeEntity.getFeeCode());
  }


}

