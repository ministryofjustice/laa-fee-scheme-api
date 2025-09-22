package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Immigration and asylum fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public final class ImmigrationAsylumFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by ImmigrationAsylumFeeCalculator and not available via FeeCalculatorFactory
  }

  private ImmigrationAsylumFixedFeeCalculator() {}

  private static final List<String> FEE_CODES_WITH_NO_DISBURSEMENT = List.of("IDAS1", "IDAS2");

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  /**
   * Calculated fee for Immigration and asylum fee based on the provided fee entity and fee calculation request.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Immigration and Asylum fixed fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    log.info("Get fields from fee calculation request");

    String claimId = feeCalculationRequest.getClaimId();

    // get the requested disbursement amount from feeCalculationRequest
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    // get the requested disbursement VAT amount from feeCalculationRequest
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // get the requested detentionAndTravelCosts amount from feeCalculationRequest
    BigDecimal detentionAndTravelCosts = toBigDecimal(feeCalculationRequest.getDetentionAndWaitingCosts());

    // get the requested jrFormFilling amount from feeCalculationRequest
    BigDecimal jrFormFillingCosts = toBigDecimal(feeCalculationRequest.getJrFormFilling());

    // get the bolt fee details from util class
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);

    BigDecimal netDisbursementAmount;
    // If fee code is "IDAS1", "IDAS2", and a requestedNetDisbursementAmount exists, return a warning, as these codes
    // are exempt from claiming disbursement
    if (isDisbursementNotAllowed(feeEntity, requestedNetDisbursementAmount)) {
      log.warn("Disbursement not allowed for fee calculation");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_CODE_DESCRIPTION)
          .type(WARNING)
          .build());
      disbursementVatAmount = BigDecimal.ZERO;
      netDisbursementAmount = BigDecimal.ZERO;
    } else {
      log.info("Check disbursement for fee calculation");
      BigDecimal netDisbursementLimit = feeEntity.getDisbursementLimit();
      netDisbursementAmount = getNetDisbursement(requestedNetDisbursementAmount, netDisbursementLimit, feeCalculationRequest);
    }

    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(jrFormFillingCosts)
        .add(detentionAndTravelCosts)
        .add(toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()));

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    log.info("Calculate total amount for fee calculation");
    BigDecimal finalTotal = fixedFeeAndAdditionalCosts
        .add(calculatedVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    log.info("Build fee calculation response");
    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(finalTotal))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(netDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(disbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .detentionAndWaitingCostsAmount(toDouble(detentionAndTravelCosts))
        .jrFormFillingAmount(toDouble(jrFormFillingCosts))
        .boltOnFeeDetails(boltOnFeeDetails)
        .build();

    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(claimId)
        .validationMessages(validationMessages)
        .escapeCaseFlag(false) // temp hard coded
        .feeCalculation(feeCalculation)
        .build();
  }

  /**
   * Calculate net disbursement amount based on requested amount, limit and prior authority.
   */
  private static BigDecimal getNetDisbursement(BigDecimal requestedNetDisbursementAmount, BigDecimal netDisbursementLimit,
                                               FeeCalculationRequest feeData) {

    if (requestedNetDisbursementAmount.compareTo(netDisbursementLimit) <= 0) {
      // Where requestedNetDisbursementAmount is below limit, we allow request as is.
      log.info("Disbursement is below limit for fee calculation");
      return requestedNetDisbursementAmount;
    }

    // Where requestedNetDisbursementAmount is above limit, we allow request as is, if they have authorisation,
    // if no authorisation default to limit.
    if (feeData.getImmigrationPriorAuthorityNumber() != null) {
      log.info("Disbursement above limit with authorisation");
      return requestedNetDisbursementAmount;
    }

    log.info("Disbursement above limit without authorisation capped to limit: {}", netDisbursementLimit);
    return netDisbursementLimit;
  }

  /**
   * determine if fee code is exempt from requesting disbursement.
   */
  private static boolean isDisbursementNotAllowed(FeeEntity feeEntity, BigDecimal requestedAmount) {
    return requestedAmount.compareTo(BigDecimal.ZERO) >= 0 && FEE_CODES_WITH_NO_DISBURSEMENT.contains(feeEntity.getFeeCode());
  }

}

