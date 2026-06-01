package uk.gov.justice.laa.fee.scheme.feecalculator.disbursement;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.validateAndCapDisbursementVat;
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
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Mental Health disbursement only fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentalHealthDisbursementOnlyCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @param feeEntity             the fee entity containing fee details
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Mental Health disbursements only");

    BigDecimal requestNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    // Calculate VAT rate for disbursement VAT cap
    LocalDate claimStartDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(claimStartDate, vatIndicator);

    // Validate and cap disbursement VAT
    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    BigDecimal disbursementVatAmount = validateAndCapDisbursementVat(
        requestNetDisbursementAmount, requestedDisbursementVatAmount, vatRate, validationMessages);

    BigDecimal totalAmount = calculateTotalAmount(requestNetDisbursementAmount, disbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(toDoubleOrNull(disbursementVatAmount))
        .requestedDisbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages);
  }
}
