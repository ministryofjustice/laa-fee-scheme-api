package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVOCACY_APPEALS_REVIEWS;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDateAdvocacyAppealsReviews;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

/**
 * Calculate the Advocacy Assistance in the Crown Court or Appeals & Reviews hourly rate fee,
 * for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class AdvocacyAppealsReviewsHourlyRateCalculator implements FeeCalculator {

  public static final String WARNING_CODE_DESCRIPTION = "test warning message, limit exceeded";

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
      log.info("profit and Additional Costs have exceeded upper cost limit");
      validationMessages.add(ValidationMessagesInner.builder()
          .message(WARNING_CODE_DESCRIPTION)
          .type(WARNING)
          .build());
    }

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = getFeeClaimStartDateAdvocacyAppealsReviews(feeCalculationRequest);
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
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(requestedNetDisbursementAmount))
            .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
            .disbursementVatAmount(toDouble(requestedNetDisbursementVatAmount))
            .hourlyTotalAmount(toDouble(profitAndAdditionalCosts))
            .netProfitCostsAmount(toDouble(requestedNetProfitCosts))
            .requestedNetProfitCostsAmount(toDouble(requestedNetProfitCosts))
            .netWaitingCosts(toDouble(requestedWaitingCosts))
            .netTravelCostsAmount(toDouble(requestedTravelCosts))
            .build())
        .build();
  }
}