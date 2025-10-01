package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGS_COURT_UNDESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT_DESIGNATED;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT_UNDESIGNATED;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the designated magistrates or youth court fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class MagsAndYouthCourtFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(
        MAGS_COURT_DESIGNATED,
        MAGS_COURT_UNDESIGNATED,
        YOUTH_COURT_DESIGNATED,
        YOUTH_COURT_UNDESIGNATED
    );
  }

  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest request, FeeEntity feeEntity) {
    CategoryType categoryType = feeEntity.getCategoryType();

    return switch (categoryType) {
      case MAGS_COURT_DESIGNATED, YOUTH_COURT_DESIGNATED -> calculateDesignated(request, feeEntity);
      case MAGS_COURT_UNDESIGNATED, YOUTH_COURT_UNDESIGNATED -> calculateUndesignated(request, feeEntity);
      default -> throw new IllegalStateException("Unexpected category: " + categoryType);
    };
  }

  private FeeCalculationResponse calculateDesignated(FeeCalculationRequest request, FeeEntity feeEntity) {
    log.info("Calculate magistrates and youth court designated fixed fee");
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(request.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(request.getDisbursementVatAmount());

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    LocalDate startDate = request.getStartDate();
    Boolean vatApplicable = request.getVatIndicator();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFeeAmount, startDate, vatApplicable);

    BigDecimal finalTotal = fixedFeeAmount
        .add(calculatedVatAmount)
        .add(requestedNetDisbursementAmount)
        .add(requestedDisbursementVatAmount);

    return buildResponse(
        request,
        feeEntity,
        finalTotal,
        calculatedVatAmount,
        requestedNetDisbursementAmount,
        requestedDisbursementVatAmount,
        fixedFeeAmount,
        null,
        null
    );
  }

  private FeeCalculationResponse calculateUndesignated(FeeCalculationRequest request, FeeEntity feeEntity) {
    log.info("Calculate magistrates and youth court undesignated fixed fee");
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(request.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(request.getDisbursementVatAmount());
    BigDecimal requestedTravelCosts = toBigDecimal(request.getNetTravelCosts());
    BigDecimal requestedWaitingCosts = toBigDecimal(request.getNetWaitingCosts());

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(requestedTravelCosts)
        .add(requestedWaitingCosts);

    LocalDate startDate = request.getStartDate();
    Boolean vatApplicable = request.getVatIndicator();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    BigDecimal finalTotal = fixedFeeAndAdditionalCosts
        .add(calculatedVatAmount)
        .add(requestedNetDisbursementAmount)
        .add(requestedDisbursementVatAmount);

    return buildResponse(
        request,
        feeEntity,
        finalTotal,
        calculatedVatAmount,
        requestedNetDisbursementAmount,
        requestedDisbursementVatAmount,
        fixedFeeAmount,
        requestedTravelCosts,
        requestedWaitingCosts
    );
  }

  private FeeCalculationResponse buildResponse(
      FeeCalculationRequest request,
      FeeEntity feeEntity,
      BigDecimal finalTotal,
      BigDecimal calculatedVatAmount,
      BigDecimal requestedNetDisbursementAmount,
      BigDecimal requestedDisbursementVatAmount,
      BigDecimal fixedFeeAmount,
      BigDecimal requestedTravelCosts,
      BigDecimal requestedWaitingCosts
  ) {
    LocalDate startDate = request.getStartDate();
    Boolean vatApplicable = request.getVatIndicator();

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(finalTotal))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .netWaitingCosts(nonNull(requestedTravelCosts) ? toDouble(requestedWaitingCosts) : null)
        .netTravelCostsAmount(nonNull(requestedTravelCosts) ? toDouble(requestedTravelCosts) : null)
        .build();

    return FeeCalculationResponse.builder()
        .feeCode(request.getFeeCode())
        .schemeId(feeEntity.getFeeSchemeCode().getSchemeCode())
        .claimId(request.getClaimId())
        .feeCalculation(feeCalculation)
        .build();
  }
}