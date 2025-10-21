package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
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
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate Education fee for a given fee entity and fee data.
 */
@Slf4j
@Component
public class EducationFixedFeeCalculator implements FeeCalculator {

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

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

    log.info("Calculate Standard fixed fee");

    // Fixed fee calculation
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee,
        calculatedVatAmount, netDisbursementAmount, disbursementVatAmount);

    // Escape case logic
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean isEscaped = FeeCalculationUtil.isEscapedCase(netProfitCosts, feeEntity.getEscapeThresholdLimit());

    if (isEscaped) {
      log.warn("Fee total exceeds escape threshold limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_CODE_DESCRIPTION)
          .type(WARNING)
          .build());
    }

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .escapeCaseFlag(isEscaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(netDisbursementAmount))
            .requestedNetDisbursementAmount(toDouble(netDisbursementAmount))
            .disbursementVatAmount(toDouble(disbursementVatAmount))
            .netProfitCostsAmount(toDouble(netProfitCosts))
            .fixedFeeAmount(toDouble(fixedFee))
            .build())
        .build();
  }
}
