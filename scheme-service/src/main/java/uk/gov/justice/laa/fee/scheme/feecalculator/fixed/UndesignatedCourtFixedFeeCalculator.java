package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDoubleOrNull;

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
 * Calculate the undesignated magistrates or youth court fee for a given fee entity and fee calculation request.
 */
@Slf4j
@Component
public class UndesignatedCourtFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of();
  }

  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {
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
        .vatRateApplied(toDoubleOrNull(getVatRateForDate(startDate, vatApplicable)))
        .calculatedVatAmount(toDouble(calculatedVatAmount))
        .disbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .requestedNetDisbursementAmount(feeCalculationRequest.getNetDisbursementAmount())
        .disbursementVatAmount(feeCalculationRequest.getDisbursementVatAmount())
        .fixedFeeAmount(toDouble(fixedFeeAmount))
        .netWaitingCostsAmount(feeCalculationRequest.getNetWaitingCosts())
        .netTravelCostsAmount(feeCalculationRequest.getNetTravelCosts())
        .build();

    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .feeCalculation(feeCalculation)
        .build();
  }

}
