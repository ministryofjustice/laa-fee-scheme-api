package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRE_ORDER_COVER;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_PREORDER_COVER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.buildFeeCalculationResponse;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.calculateTotalAmount;
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
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

/**
 * Calculate the Pre Order Cover hourly rate fee,
 * for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PreOrderCoverHourlyRateCalculator implements FeeCalculator {

  private final VatRatesService vatRatesService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(PRE_ORDER_COVER);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Pre Order Cover hourly rate fee");

    List<ValidationMessagesInner> validationMessages = new ArrayList<>();
    BigDecimal upperCostLimit = feeEntity.getUpperCostLimit();

    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedNetDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    BigDecimal requestedTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());
    BigDecimal requestedWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal profitAndAdditionalCosts = requestedNetProfitCosts
        .add(requestedTravelCosts)
        .add(requestedWaitingCosts);

    if (profitAndAdditionalCosts.add(requestedNetDisbursementAmount).compareTo(upperCostLimit) > 0) {
      throw new ValidationException(ERR_CRIME_PREORDER_COVER_UPPER_LIMIT, new FeeContext(feeCalculationRequest));
    }

    // Calculate VAT if applicable
    Boolean vatIndicator = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = getFeeClaimStartDate(PRE_ORDER_COVER, feeCalculationRequest);
    BigDecimal vatRate = vatRatesService.getVatRateForDate(startDate, vatIndicator);
    BigDecimal calculatedVatAmount = calculateVatAmount(profitAndAdditionalCosts, vatRate);

    BigDecimal totalAmount = calculateTotalAmount(profitAndAdditionalCosts,
        calculatedVatAmount, requestedNetDisbursementAmount, requestedNetDisbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
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
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation, validationMessages);
  }
}