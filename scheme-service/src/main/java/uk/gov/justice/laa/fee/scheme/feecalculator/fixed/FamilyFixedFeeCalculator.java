package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the fixed fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class FamilyFixedFeeCalculator implements FeeCalculator {

  private static final String ESCAPE_CASE_WARNING_CODE_DESCRIPTION = "123warning";

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

    log.info("Calculate Standard fixed fee");

    log.info("Get fields from fee calculation request");
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate claimStartDate = FeeCalculationUtil.getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);

    BigDecimal fixedFee = feeEntity.getFixedFee();

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);
    BigDecimal netDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal disbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal finalTotal = fixedFee
        .add(fixedFeeVatAmount)
        .add(netDisbursementAmount)
        .add(disbursementVatAmount);

    boolean isClaimEscaped = FeeCalculationUtil.isEscapedCase(finalTotal, feeEntity.getEscapeThresholdLimit());

    List<ValidationMessagesInner> validationMessages = null;

    if (isClaimEscaped) {
      validationMessages = new ArrayList<>();
      log.warn("Fee total exceeds escape threshold limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(ESCAPE_CASE_WARNING_CODE_DESCRIPTION)
          .type(WARNING)
          .build());
    }

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(isClaimEscaped ? validationMessages : List.of())
        .escapeCaseFlag(isClaimEscaped)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(finalTotal))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(claimStartDate, vatApplicable)))
            .calculatedVatAmount(toDouble(fixedFeeVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .fixedFeeAmount(toDouble(fixedFee))
            .build())
        .build();
  }
}
