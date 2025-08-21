package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.NumberUtility.toDouble;
import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility.getVatRateForDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.BoltOnUtility;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.VatUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.Warning;

/**
 * Calculate the Immigration and asylum fee for a given fee entity and fee calculation request.
 */
public final class ImmigrationAndAsylumFixedFeeCalculator {

  private ImmigrationAndAsylumFixedFeeCalculator() {
  }

  private static final List<String> FEE_CODES_WITH_NO_DISBURSEMENT = List.of("IDAS1", "IDAS2");
  private static final String WARNING_CODE = "123"; // clarify what code should be
  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  /**
   * Calculated fee for Immigration and asylum fee based on the provided fee entity and fee calculation request.
   */
  public static FeeCalculationResponse getFee(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {
    LocalDate startDate = feeCalculationRequest.getStartDate();
    boolean vatApplicable = Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator());

    // get the requested disbursement amount from feeCalculationRequest
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    // get the requested disbursement VAT amount from feeCalculationRequest
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // get the requested detentionAndTravelCosts amount from feeCalculationRequest
    BigDecimal detentionAndTravelCosts = toBigDecimal(feeCalculationRequest.getDetentionAndWaitingCosts());
    BigDecimal detentionAndTravelVatCosts = VatUtility.getVatValue(detentionAndTravelCosts, startDate, vatApplicable);

    // get the requested jrFormFilling amount from feeCalculationRequest
    BigDecimal jrFormFillingCosts = toBigDecimal(feeCalculationRequest.getJrFormFilling());
    BigDecimal jrFormFillingVatCosts = VatUtility.getVatValue(jrFormFillingCosts, startDate, vatApplicable);

    // get the total bolt on value amount from utility class
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);
    BigDecimal boltOnVatValue = VatUtility.getVatValue(boltOnValue, startDate, vatApplicable);

    BigDecimal netDisbursementAmount;
    BigDecimal netDisbursementLimit = feeEntity.getDisbursementLimit();
    Warning warning = null;
    // If fee code is "IDAS1", "IDAS2", and a requestedNetDisbursementAmount exists, return a warning, as these codes
    // are exempt from claiming disbursement
    if (isDisbursementNotAllowed(feeEntity, requestedNetDisbursementAmount)) {
      warning = Warning.builder()
          .warningCode(WARNING_CODE)
          .warningDescription(WARNING_CODE_DESCRIPTION)
          .build();
      disbursementVatAmount = BigDecimal.ZERO;
      netDisbursementAmount = BigDecimal.ZERO;
    } else {
      netDisbursementAmount = getNetDisbursement(requestedNetDisbursementAmount, netDisbursementLimit, feeCalculationRequest);
    }

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeVatAmount = VatUtility.getVatValue(fixedFeeAmount, startDate, vatApplicable);


    BigDecimal finalTotalWithoutVat = fixedFeeAmount
        .add(detentionAndTravelCosts)
        .add(jrFormFillingCosts)
        .add(boltOnValue)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    BigDecimal finalTotalWithVat = BigDecimal.ZERO;
    if (vatApplicable) {
      finalTotalWithVat = finalTotalWithoutVat
          .add(fixedFeeVatAmount)
          .add(detentionAndTravelVatCosts)
          .add(jrFormFillingVatCosts)
          .add(boltOnVatValue);
    }

    return new FeeCalculationResponse().toBuilder()
        .warning(warning)
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId("temp hard coded")
        .escapeCaseFlag(false) // temp hard coded
        .feeCalculationItems(FeeCalculation.builder()
            .calculatedClaimAmount(toDouble(vatApplicable ? finalTotalWithVat : finalTotalWithoutVat))
            .subTotal(toDouble(finalTotalWithoutVat.subtract(disbursementVatAmount)))
            .vatIndicator(Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator()))
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .fixedFeeVatAmount(toDouble(fixedFeeVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .detentionAndWaitingCostsAmount(toDouble(detentionAndTravelCosts))
            .detentionAndWaitingCostsVatAmount(toDouble(detentionAndTravelVatCosts))
            .jrFormFillingAmount(toDouble(jrFormFillingCosts))
            .jrFormFillingVatAmount(toDouble(jrFormFillingVatCosts))
            .boltOnFeeTotal(toDouble(boltOnValue))
            .boltOnFeeVatTotal(toDouble(boltOnVatValue))
            .build())
        .build();
  }

  /**
   * Calculate net disbursement amount based on requested amount, limit and prior authority.
   */
  private static BigDecimal getNetDisbursement(BigDecimal requestedNetDisbursementAmount, BigDecimal netDisbursementLimit,
                                               FeeCalculationRequest feeData) {

    if (requestedNetDisbursementAmount.compareTo(netDisbursementLimit) <= 0) {
      // Where requestedNetDisbursementAmount is below limit, we allow request as is.
      return requestedNetDisbursementAmount;
    }
    // Where requestedNetDisbursementAmount is above limit, we allow request as is, if they have authorisation,
    // if no authorisation default to limit.
    return feeData.getDisbursementPriorAuthority() != null
        ? requestedNetDisbursementAmount
        : netDisbursementLimit;
  }

  /**
   * determine if fee code is exempt from requesting disbursement.
   */
  private static boolean isDisbursementNotAllowed(FeeEntity feeEntity, BigDecimal requestedAmount) {
    return requestedAmount.compareTo(BigDecimal.ZERO) >= 0 && FEE_CODES_WITH_NO_DISBURSEMENT.contains(feeEntity.getFeeCode());
  }

}

