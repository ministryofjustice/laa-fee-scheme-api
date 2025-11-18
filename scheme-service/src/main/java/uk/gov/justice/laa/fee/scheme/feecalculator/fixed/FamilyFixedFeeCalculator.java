package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_FAMILY_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.defaultToZeroIfNull;
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
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FamilyFixedFeeCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(FAMILY);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Family fixed fee");

    // Get fixed fee amount
    BigDecimal fixedFeeAmount = defaultToZeroIfNull(feeEntity.getFixedFee());

    // Calculate VAT if applicable
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatApplicable);
    BigDecimal calculatedVatAmount = calculateVatAmount(fixedFeeAmount, vatRate);

    // Get disbursements
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal totalAmount = calculateTotalAmount(fixedFeeAmount, calculatedVatAmount,
        netDisbursementAmount, disbursementVatAmount);

    boolean isClaimEscaped = false;

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    // For five Fee Codes FVP012, FVP010, FVP190, FVP200 & FVP210, Escape threshold limit will be empty.
    // Hence, No escape threshold check for these fee codes.

    BigDecimal escapeThresholdLimit = feeEntity.getEscapeThresholdLimit();

    if (escapeThresholdLimit != null) {

      BigDecimal netProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());

      isClaimEscaped = FeeCalculationUtil.isEscapedCase(totalAmount.add(netProfitCosts), escapeThresholdLimit);

      if (isClaimEscaped) {
        validationMessages.add(buildValidationWarning(WARN_FAMILY_ESCAPE_THRESHOLD,
            "Fee total exceeds escape threshold limit"));
      }
    }

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(isClaimEscaped ? validationMessages : List.of())
        .escapeCaseFlag(isClaimEscaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(vatRate))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .build())
        .build();
  }
}
