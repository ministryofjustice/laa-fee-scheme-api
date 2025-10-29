package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Associated Civil Work fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class AssociatedCivilFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.ASSOCIATED_CIVIL);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Associated Civil fixed fee");

    // Fixed fee calculation
    BigDecimal fixedFee = defaultToZeroIfNull(feeEntity.getFixedFee());
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(CategoryType.ASSOCIATED_CIVIL, feeCalculationRequest);
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal calculatedVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee,
        calculatedVatAmount, netDisbursementAmount, disbursementVatAmount);

    // Escape case logic
    BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal netTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());
    BigDecimal netWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal feeTotal = netProfitCosts
        .add(netTravelCosts)
        .add(netWaitingCosts);

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    boolean isEscaped = FeeCalculationUtil.isEscapedCase(feeTotal, feeEntity.getEscapeThresholdLimit());

    if (isEscaped) {
      validationMessages.add(buildValidationWarning(WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD,
          "Fee total exceeds escape threshold limit"));
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
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(claimStartDate, vatApplicable)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFee))
            .build())
        .build();
  }
}
