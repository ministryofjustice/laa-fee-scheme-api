package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.filterBoltOnFeeDetails;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isEscapedCase;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
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
 * Calculate the Mental Health fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class MentalHealthFixedFeeCalculator implements FeeCalculator {

  // @TODO: TBC during error and validation work, and likely moved to common util
  public static final String WARNING_MESSAGE_WARMH1 = "The claim exceeds the Escape Case Threshold. An Escape Case Claim "
      + "must be submitted for further costs to be paid.";

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
    BigDecimal requestNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()));

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(
        fixedFeeAndAdditionalCosts,
        calculatedVatAmount,
        requestNetDisbursementAmount,
        requestedDisbursementVatAmount
    );

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean escapeCaseFlag = false;
    if (nonNull(feeEntity.getEscapeThresholdLimit())) {
      escapeCaseFlag = isEscaped(feeCalculationRequest, feeEntity, boltOnFeeDetails, validationMessages);
    }

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(escapeCaseFlag)
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(startDate, vatApplicable)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .boltOnFeeDetails(filterBoltOnFeeDetails(boltOnFeeDetails))
            .build())
        .build();
  }

  private boolean isEscaped(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                               BoltOnFeeDetails boltOnFeeDetails, List<ValidationMessagesInner> validationMessages) {

    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal totalA = requestedNetProfitCosts.add(requestedNetCostOfCounsel);

    BigDecimal escapeCaseThreshold = feeEntity.getEscapeThresholdLimit();
    BigDecimal requestedBoltOnTotalAmount = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
    BigDecimal totalB = escapeCaseThreshold.add(requestedBoltOnTotalAmount);

    if (isEscapedCase(totalA, totalB)) {
      log.warn("Case has escaped");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WarningType.WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD.getMessage())
          .code(WarningType.WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD.getCode())
          .type(WARNING)
          .build());
      return true;
    } else {
      log.warn("Case has not escaped");
      return false;
    }
  }
}
