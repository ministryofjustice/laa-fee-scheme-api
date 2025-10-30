package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_HAS_ESCAPED;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_MAY_HAVE_ESCAPED;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
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
        .claimId(feeCalculationRequest.getClaimId())
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
   * Calculate if the case may have escaped using fee limit,
   * escape flag will always be false.
   */
  private void feeLimitValidation(FeeEntity feeEntity, List<ValidationMessagesInner> validationMessages,
                                     BigDecimal totalAmount) {

    BigDecimal feeLimit = feeEntity.getTotalLimit();
    if (isEscapedCase(totalAmount, feeLimit)) {
      validationMessages.add(buildValidationWarning(WARN_PRISON_MAY_HAVE_ESCAPED,
          "Case has exceeded fee limit"));
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
      validationMessages.add(buildValidationWarning(WARN_PRISON_HAS_ESCAPED,
          "Case has escaped"));
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
        .vatRateApplied(toDoubleOrNull(getVatRateForDate(claimStartDate, vatApplicable)))
        .calculatedVatAmount(toDouble(fixedFeeVatAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .fixedFeeAmount(toDouble(fixedFee))
        .build();
  }

}