package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

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
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the prison law fee for a given fee entity and fee data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrisonLawFixedFeeCalculator implements FeeCalculator {

  private static final Set<String> FEE_CODES_ESCAPE_USING_ESCAPE_THRESHOLD = Set.of("PRIA", "PRIB2", "PRIC2", "PRID2", "PRIE2");
  private static final Set<String> FEE_CODES_ESCAPE_USING_FEE_LIMIT = Set.of("PRIB1", "PRIC1", "PRID1", "PRIE1");
  // @TODO: TBC during error and validation work, and likely moved to common util
  public static final String WARNING_MESSAGE_WARCRM5 = "Costs are included. Profit and Waiting Costs exceed the Lower "
      + "Standard Fee Limit. An escape fee may be payable.";
  public static final String WARNING_MESSAGE_WARCRM6 = "The claim exceeds the Escape Case Threshold. An Escape Case Claim "
      + "must be submitted for further costs to be paid.";

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.PRISON_LAW);
  }

  /**
   * Determines the calculation based on prison law fee code.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Prison Law fixed fee");

    return calculateFeesUsingFeeCode(feeEntity, feeCalculationRequest);

  }

  private FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                           FeeCalculationRequest feeCalculationRequest) {

    log.info("Calculate fixed fee and costs using fee entity");
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    FeeCalculation feeCalculation = mapFeeCalculation(feeCalculationRequest, feeEntity.getFixedFee());
    boolean escapeCaseFlag = isEscapedOrPastFeeLimit(feeCalculationRequest, feeEntity, validationMessages);

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .escapeCaseFlag(escapeCaseFlag)
        .validationMessages(validationMessages)
        .feeCalculation(feeCalculation).build();
  }

  /**
   * Calculate if case has escaped.
   */
  private boolean isEscapedOrPastFeeLimit(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                          List<ValidationMessagesInner> validationMessages) {

    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());
    BigDecimal totalAmount = requestedNetProfitCosts.add(requestedNetWaitingCosts);

    if (FEE_CODES_ESCAPE_USING_ESCAPE_THRESHOLD.contains(feeCalculationRequest.getFeeCode())) {
      return escapeCaseValidation(feeEntity, validationMessages, totalAmount);
    } else if (FEE_CODES_ESCAPE_USING_FEE_LIMIT.contains(feeCalculationRequest.getFeeCode())) {
      feeLimitValidation(feeEntity, validationMessages, totalAmount);
      return false;
    }
    return false;
  }

  /**
   * Calculate if the  case may have escaped using fee limit,
   * escape flag will always be false.
   */
  private void feeLimitValidation(FeeEntity feeEntity, List<ValidationMessagesInner> validationMessages,
                                     BigDecimal totalAmount) {

    BigDecimal feeLimit = feeEntity.getTotalLimit();
    if (isEscapedCase(totalAmount, feeLimit)) {
      log.warn("Case has exceeded fee limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_MESSAGE_WARCRM5)
          .type(WARNING)
          .build());
    } else {
      log.warn("Case has not exceeded fee limit");
    }
  }

  /**
   * Calculate if the has escaped using EscapeThresholdLimit,
   * escape flag will be true when it has.
   */
  private boolean escapeCaseValidation(FeeEntity feeEntity, List<ValidationMessagesInner> validationMessages,
                                       BigDecimal totalAmount) {

    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();
    if (isEscapedCase(totalAmount, escapeThresholdLimit)) {
      log.warn("Case has escaped");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_MESSAGE_WARCRM6)
          .type(WARNING)
          .build());
      return true;
    }
    log.warn("Case has not escaped");
    return false;
  }

  /**
   * Mapping fee elements into Fee Calculation.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @param fixedFee              BigDecimal
   * @return FeeCalculation
   */
  private FeeCalculation mapFeeCalculation(FeeCalculationRequest feeCalculationRequest, BigDecimal fixedFee) {

    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.PRISON_LAW, feeCalculationRequest);

    // Apply VAT where applicable
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, fixedFeeVatAmount,
        requestedNetDisbursementAmount, requestedDisbursementVatAmount);

    return FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
        .calculatedVatAmount(toDouble(fixedFeeVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFee))
        .build();
  }

}