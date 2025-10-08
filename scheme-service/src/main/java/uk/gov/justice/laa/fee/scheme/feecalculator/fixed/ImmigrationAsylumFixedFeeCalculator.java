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
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
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

  private static final List<String> FEE_CODES_WITH_NO_DISBURSEMENT_LIMIT = List.of("IDAS1", "IDAS2");
  private static final String WARNING_CODE_WARIA1 = "TEMPORARY WARIA 1 MESSAGE";
  private static final String WARNING_CODE_WARIA2 = "TEMPORARY WARIA 2 MESSAGE";

  /**
   * Calculated fee for Immigration and asylum fee based on the provided fee entity and fee calculation request.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate Immigration and Asylum fixed fee");
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    log.info("Get fields from fee calculation request");
    // get the requested disbursement amount from feeCalculationRequest
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    // get the requested disbursement VAT amount from feeCalculationRequest
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    // get the requested detentionAndTravelCosts amount from feeCalculationRequest
    BigDecimal requestedDetentionAndTravelCosts = toBigDecimal(feeCalculationRequest.getDetentionAndWaitingCosts());
    // get the requested jrFormFilling amount from feeCalculationRequest
    BigDecimal requestedJrFormFillingCosts = toBigDecimal(feeCalculationRequest.getJrFormFilling());
    // get the bolt fee details from util class
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);

    BigDecimal netDisbursementAmount;
    if (isDisbursementUnlimited(feeEntity)) {
      log.info("Disbursement added with no limit");
      netDisbursementAmount = requestedNetDisbursementAmount;
    } else {
      log.info("Check disbursement for fee calculation");
      netDisbursementAmount = getNetDisbursement(requestedNetDisbursementAmount, feeCalculationRequest,
          feeEntity, validationMessages);
    }

    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(requestedJrFormFillingCosts)
        .add(requestedDetentionAndTravelCosts)
        .add(toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()));

    // Apply VAT where applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFeeAndAdditionalCosts,
        calculatedVatAmount, netDisbursementAmount, requestedDisbursementVatAmount);

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .escapeCaseFlag(false) // temp hard coded
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
            .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .detentionAndWaitingCostsAmount(toDouble(requestedDetentionAndTravelCosts))
            .jrFormFillingAmount(toDouble(requestedJrFormFillingCosts))
            .boltOnFeeDetails(boltOnFeeDetails)
            .build())
        .build();
  }

  /**
   * Calculate net disbursement amount based on requested amount, limit and prior authority.
   */
  private static BigDecimal getNetDisbursement(BigDecimal requestedNetDisbursementAmount,
                                               FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                               List<ValidationMessagesInner> validationMessages) {

    BigDecimal netDisbursementLimit = feeEntity.getDisbursementLimit();

    if (requestedNetDisbursementAmount.compareTo(netDisbursementLimit) <= 0) {
      // Where requestedNetDisbursementAmount is below limit, we allow request as is.
      log.info("Disbursement is below limit for fee calculation");
      return requestedNetDisbursementAmount;
    }
    // Where requestedNetDisbursementAmount is above limit, we allow request as is, if they have authorisation,
    // if no authorisation default to limit.
    if (feeCalculationRequest.getImmigrationPriorAuthorityNumber() != null) {
      log.info("Disbursement above limit with authorisation");
      return requestedNetDisbursementAmount;
    }

    log.info("Disbursement above limit without authorisation capped to limit: {}", netDisbursementLimit);
    String message = ("IALB".equals(feeCalculationRequest.getFeeCode()) || "IMLB".equals(feeCalculationRequest.getFeeCode()))
        ? WARNING_CODE_WARIA2
        : WARNING_CODE_WARIA1;

    validationMessages.add(ValidationMessagesInner.builder()
        .message(message)
        .type(WARNING)
        .build());

    return netDisbursementLimit;
  }

  /**
   * determine if fee code is exempt from disbursement limiting.
   */
  private static boolean isDisbursementUnlimited(FeeEntity feeEntity) {
    return FEE_CODES_WITH_NO_DISBURSEMENT_LIMIT.contains(feeEntity.getFeeCode());
  }


}

