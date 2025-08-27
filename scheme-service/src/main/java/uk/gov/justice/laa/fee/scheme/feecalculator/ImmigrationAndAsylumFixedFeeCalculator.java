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

    // get the requested jrFormFilling amount from feeCalculationRequest
    BigDecimal jrFormFillingCosts = toBigDecimal(feeCalculationRequest.getJrFormFilling());

    // get the total bolt on value amount from utility class
    BigDecimal boltOnValue = BoltOnUtility.calculateBoltOnAmount(feeCalculationRequest, feeEntity);

    BigDecimal netDisbursementAmount;
    BigDecimal netDisbursementLimit = feeEntity.getDisbursementLimit();
    Warning warning = null;
    // If fee code is "IDAS1", "IDAS2", and a requestedNetDisbursementAmount exists, return a warning, as these codes
    // are exempt from claiming disbursement
    if (isDisbursementNotAllowed(feeEntity, requestedNetDisbursementAmount)) {
      warning = Warning.builder()
          .warningDescription(WARNING_CODE_DESCRIPTION)
          .build();
      disbursementVatAmount = BigDecimal.ZERO;
      netDisbursementAmount = BigDecimal.ZERO;
    } else {
      netDisbursementAmount = getNetDisbursement(requestedNetDisbursementAmount, netDisbursementLimit, feeCalculationRequest);
    }

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal calculatedVatValue = VatUtility.getVatValue(fixedFeeAmount
        .add(detentionAndTravelCosts)
        .add(jrFormFillingCosts)
        .add(boltOnValue), startDate, vatApplicable);

    BigDecimal finalTotal = fixedFeeAmount
        .add(jrFormFillingCosts)
        .add(detentionAndTravelCosts)
        .add(boltOnValue)
        .add(calculatedVatValue)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId("temp hard coded")
        .warning(warning)
        .escapeCaseFlag(false) // temp hard coded
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(Boolean.TRUE.equals(feeCalculationRequest.getVatIndicator()))
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatValue))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .detentionAndWaitingCostsAmount(toDouble(detentionAndTravelCosts))
            .jrFormFillingAmount(toDouble(jrFormFillingCosts))
            .boltOnFeeAmount(toDouble(boltOnValue))
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

