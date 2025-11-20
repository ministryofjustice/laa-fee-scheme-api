package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.LimitType.DISBURSEMENT;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_400_LEGAL_HELP;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_DISB_600_CLR;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_IMM_ASYLM_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.checkLimitAndCapIfExceeded;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.filterBoltOnFeeDetails;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.LimitContext;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Immigration and asylum fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class ImmigrationAsylumFixedFeeCalculator implements FeeCalculator {

  private static final Set<String> FEE_CODES_NO_DISBURSEMENT_LIMIT_AND_NO_ESCAPE = Set.of("IDAS1", "IDAS2");
  private static final Set<String> FEE_CODES_WITH_SUBSTANTIVE_HEARING = Set.of("IACB", "IACC", "IACF", "IMCB", "IMCC", "IMCD");
  private static final Set<String> NO_COUNSEL_FEE_CODES = Set.of("IALB", "IMLB");

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(); // Only used by ImmigrationAsylumFeeCalculator and not available via FeeCalculatorFactory
  }

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
    // get the requested detentionTravelAndWaitingCosts amount from feeCalculationRequest
    BigDecimal requestedDetentionTravelAndWaitingCosts = toBigDecimal(feeCalculationRequest.getDetentionTravelAndWaitingCosts());
    // get the requested jrFormFilling amount from feeCalculationRequest
    BigDecimal requestedJrFormFillingCosts = toBigDecimal(feeCalculationRequest.getJrFormFilling());
    // get the bolt fee details from util class
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
    // get the immigration Prior Authority Number
    String immigrationPriorAuthorityNumber = feeCalculationRequest.getImmigrationPriorAuthorityNumber();

    BigDecimal netDisbursementAmount;
    if (FEE_CODES_NO_DISBURSEMENT_LIMIT_AND_NO_ESCAPE.contains(feeEntity.getFeeCode())) {
      log.info("Disbursement added with no limit");
      netDisbursementAmount = requestedNetDisbursementAmount;
    } else {
      log.info("Check disbursement for fee calculation");

      WarningType warning = (("IALB".equals(feeCalculationRequest.getFeeCode()) || "IMLB".equals(feeCalculationRequest.getFeeCode()))
          ? WARN_IMM_ASYLM_DISB_400_LEGAL_HELP
          : WARN_IMM_ASYLM_DISB_600_CLR);
      LimitContext disbursementLimitContext = new LimitContext(DISBURSEMENT, feeEntity.getDisbursementLimit(),
          immigrationPriorAuthorityNumber, warning);

      netDisbursementAmount = checkLimitAndCapIfExceeded(requestedNetDisbursementAmount, disbursementLimitContext, validationMessages);
    }

    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(requestedJrFormFillingCosts)
        .add(requestedDetentionTravelAndWaitingCosts)
        .add(toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()));

    // Calculate VAT if applicable
    LocalDate startDate = feeCalculationRequest.getStartDate();
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAndAdditionalCosts, vatRate);

    // Calculate total amount
    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAndAdditionalCosts,
        calculatedVatAmount, netDisbursementAmount, requestedDisbursementVatAmount);

    boolean escapeCaseFlag = false;
    if (!FEE_CODES_NO_DISBURSEMENT_LIMIT_AND_NO_ESCAPE.contains(feeCalculationRequest.getFeeCode())) {
      log.info("calculate if case has escaped");
      escapeCaseFlag = isEscaped(feeCalculationRequest, feeEntity, boltOnFeeDetails, validationMessages);
    }

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .escapeCaseFlag(escapeCaseFlag)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatIndicator)
            .vatRateApplied(toDoubleOrNull(vatRate))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(nonNull(feeCalculationRequest.getNetDisbursementAmount()) ? toDouble(netDisbursementAmount) : null)
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .detentionTravelAndWaitingCostsAmount(feeCalculationRequest.getDetentionTravelAndWaitingCosts())
            .jrFormFillingAmount(feeCalculationRequest.getJrFormFilling())
            .boltOnFeeDetails(filterBoltOnFeeDetails(boltOnFeeDetails))
            .build())
        .build();
  }

  /**
   * Calculate if case has escaped (excluding "IDAS1", "IDAS2").
   */
  private boolean isEscaped(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity, BoltOnFeeDetails boltOnFeeDetails,
                            List<ValidationMessagesInner> validationMessages) {

    String feeCode = feeCalculationRequest.getFeeCode();
    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    // gross total
    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetCounselCosts = NO_COUNSEL_FEE_CODES.contains(feeCode)
        ? BigDecimal.ZERO
        : toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal grossTotal = requestedNetProfitCosts.add(requestedNetCounselCosts);

    // additional payments
    BigDecimal requestedBoltOnCosts = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
    BigDecimal substantiveBoltOnCost = FEE_CODES_WITH_SUBSTANTIVE_HEARING.contains(feeCode)
        ? feeEntity.getSubstantiveHearingBoltOn()
        : BigDecimal.ZERO;
    BigDecimal additionalPayments = requestedBoltOnCosts.add(substantiveBoltOnCost);
    BigDecimal totalAmount = grossTotal.subtract(additionalPayments);

    if (isEscapedCase(totalAmount, escapeThresholdLimit)) {
      log.warn("Case has escaped");
      validationMessages.add(ValidationMessagesInner.builder()
          .code(WARN_IMM_ASYLM_ESCAPE_THRESHOLD.getCode())
          .message(WARN_IMM_ASYLM_ESCAPE_THRESHOLD.getMessage())
          .type(WARNING)
          .build());
      return true;
    } else {
      log.warn("Case has not escaped");
      return false;
    }
  }
}

