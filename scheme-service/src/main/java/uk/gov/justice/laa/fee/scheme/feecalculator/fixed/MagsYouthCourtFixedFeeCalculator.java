package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

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
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the designated/undesignated magistrates or youth court fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class MagsYouthCourtFixedFeeCalculator implements FeeCalculator {

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
    log.info("Determine calculation type using category: {}", categoryType);
    return switch (categoryType) {
      case MAGS_COURT_DESIGNATED, YOUTH_COURT_DESIGNATED -> calculateDesignated(request, feeEntity);
      case MAGS_COURT_UNDESIGNATED, YOUTH_COURT_UNDESIGNATED -> calculateUndesignated(request, feeEntity);
      default -> throw new IllegalStateException("Unexpected category: " + categoryType);
    };
  }

  private FeeCalculationResponse calculateDesignated(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate magistrates and youth court designated fixed fee");
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    LocalDate startDate = feeCalculationRequest.getRepresentationOrderDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFeeAmount, startDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFeeAmount, calculatedVatAmount,
        requestedNetDisbursementAmount, requestedDisbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation);
  }

  private FeeCalculationResponse calculateUndesignated(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
    log.info("Calculate magistrates and youth court undesignated fixed fee");
    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());
    BigDecimal requestedTravelCosts = toBigDecimal(feeCalculationRequest.getNetTravelCosts());
    BigDecimal requestedWaitingCosts = toBigDecimal(feeCalculationRequest.getNetWaitingCosts());

    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(requestedTravelCosts)
        .add(requestedWaitingCosts);

    LocalDate startDate = feeCalculationRequest.getRepresentationOrderDate();
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    BigDecimal calculatedVatAmount = getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFeeAndAdditionalCosts, calculatedVatAmount,
        requestedNetDisbursementAmount, requestedDisbursementVatAmount);

    FeeCalculation feeCalculation = FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(startDate)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .netWaitingCostsAmount(feeCalculationRequest.getNetWaitingCosts())
        .netTravelCostsAmount(feeCalculationRequest.getNetTravelCosts())
        .build();

    return buildFeeCalculationResponse(feeCalculationRequest, feeEntity, feeCalculation);
  }

  private FeeCalculationResponse buildFeeCalculationResponse(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                                             FeeCalculation feeCalculation) {
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .feeCalculation(feeCalculation)
        .build();
  }
}
