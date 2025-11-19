package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVOCACY_APPEALS_REVIEWS;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_ADVOCACY_APPEALS_REVIEWS_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildValidationWarning;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
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
 * Calculate the Advocacy Assistance in the Crown Court or Appeals & Reviews hourly rate fee,
 * for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvocacyAppealsReviewsHourlyRateCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(ADVOCACY_APPEALS_REVIEWS);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Advocacy Assistance in the Crown Court or Appeals & Reviews hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    BigDecimal upperCostLimit = toBigDecimal(feeEntity.getUpperCostLimit());

    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedNetDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    BigDecimal requestedTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());
    BigDecimal requestedWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal profitAndAdditionalCosts = requestedNetProfitCosts
        .add(requestedTravelCosts)
        .add(requestedWaitingCosts);

    if (profitAndAdditionalCosts.add(requestedNetDisbursementAmount).compareTo(upperCostLimit) > 0) {
      validationMessages.add(buildValidationWarning(WARN_ADVOCACY_APPEALS_REVIEWS_UPPER_LIMIT,
          "Profit and Additional Costs have exceeded upper cost limit"));
    }

    // Calculate VAT if applicable
    LocalDate startDate = getFeeClaimStartDate(ADVOCACY_APPEALS_REVIEWS, feeCalculationRequest);
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(profitAndAdditionalCosts, vatRate);

    // Calculate total amount
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(profitAndAdditionalCosts,
        calculatedVatAmount, requestedNetDisbursementAmount, requestedNetDisbursementVatAmount);

    log.info("Build fee calculation response");
    return new FeeCalculationResponse().toBuilder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .validationMessages(validationMessages)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatIndicator)
            .vatRateApplied(toDoubleOrNull(vatRate))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
            .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
            .hourlyTotalAmount(toDouble(profitAndAdditionalCosts))
            .netProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .requestedNetProfitCostsAmount(feeCalculationRequest.getNetProfitCosts())
            .netWaitingCostsAmount(feeCalculationRequest.getNetWaitingCosts())
            .netTravelCostsAmount(feeCalculationRequest.getNetTravelCosts())
            .build())
        .build();
  }
}