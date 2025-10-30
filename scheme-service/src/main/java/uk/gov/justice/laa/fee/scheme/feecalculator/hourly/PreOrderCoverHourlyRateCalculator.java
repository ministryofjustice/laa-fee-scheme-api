package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRE_ORDER_COVER;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_PREORDER_COVER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
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
import uk.gov.justice.laa.fee.scheme.exception.FeeContext;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Pre Order Cover hourly rate fee,
 * for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class PreOrderCoverHourlyRateCalculator implements FeeCalculator {

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
    BigDecimal upperCostLimit = feeEntity.getTotalLimit();

    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedNetDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    BigDecimal requestedTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());
    BigDecimal requestedWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal profitAndAdditionalCosts = requestedNetProfitCosts
        .add(requestedTravelCosts)
        .add(requestedWaitingCosts);

    if (profitAndAdditionalCosts.add(requestedNetDisbursementAmount).compareTo(upperCostLimit) >= 0) {
      throw new ValidationException(ERR_CRIME_PREORDER_COVER_UPPER_LIMIT, new FeeContext(feeCalculationRequest));
    }

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = getFeeClaimStartDate(PRE_ORDER_COVER, feeCalculationRequest);
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(profitAndAdditionalCosts, startDate, vatApplicable);
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
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDoubleOrNull(getVatRateForDate(startDate, vatApplicable)))
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