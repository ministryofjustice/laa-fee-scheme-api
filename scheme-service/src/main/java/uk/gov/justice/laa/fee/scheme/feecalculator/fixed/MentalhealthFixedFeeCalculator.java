package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.getFeeClaimStartDate;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil;
import uk.gov.justice.laa.fee.scheme.feecalculator.util.boltons.BoltOnUtil;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Calculate the
 */
@Slf4j
@Component
public class MentalhealthFixedFeeCalculator implements FeeCalculator {

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.MENTAL_HEALTH);
  }

  /**
   * Calculated fee based on the provided fee entity and fee calculation request.
   *
   * @param feeCalculationRequest the request containing fee calculation data
   * @return FeeCalculationResponse with calculated fee
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Get fields from fee calculation request");
    BoltOnFeeDetails boltOnFeeDetails = BoltOnUtil.calculateBoltOnAmounts(feeCalculationRequest, feeEntity);
    BigDecimal requestNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());
    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    log.info("Calculate fixed fee and costs");
    BigDecimal fixedFeeAmount = feeEntity.getFixedFee();
    BigDecimal fixedFeeAndAdditionalCosts = fixedFeeAmount
        .add(toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount()));

    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();
    LocalDate startDate = getFeeClaimStartDate(feeEntity.getCategoryType(), feeCalculationRequest);
    BigDecimal calculatedVatAmount = VatUtil.getVatAmount(fixedFeeAndAdditionalCosts, startDate, vatApplicable);

    log.info("Calculate total fee amount with any disbursements, bolt ons and VAT where applicable");
    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(
        fixedFeeAndAdditionalCosts,
        calculatedVatAmount,
        requestNetDisbursementAmount,
        requestedDisbursementVatAmount
    );

    boolean mentalHealthEscaped = false;
    if (nonNull(feeEntity.getEscapeThresholdLimit())) {
      mentalHealthEscapeCalculation(feeCalculationRequest, feeEntity, boltOnFeeDetails, fixedFeeAmount, totalAmount);
    }

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .claimId(feeCalculationRequest.getClaimId())
        .escapeCaseFlag(false)
        .feeCalculation(FeeCalculation.builder()
            .totalAmount(toDouble(totalAmount))
            .vatIndicator(vatApplicable)
            .vatRateApplied(toDouble(getVatRateForDate(startDate)))
            .calculatedVatAmount(toDouble(calculatedVatAmount))
            .disbursementAmount(toDouble(requestNetDisbursementAmount))
            .requestedNetDisbursementAmount(toDouble(requestNetDisbursementAmount))
            .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
            .fixedFeeAmount(toDouble(fixedFeeAmount))
            .boltOnFeeDetails(boltOnFeeDetails)
            .build())
        .build();
  }

  private BigDecimal mentalHealthEscapeCalculation(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity,
                                                   BoltOnFeeDetails boltOnFeeDetails, BigDecimal fixedFeeAmount, BigDecimal totalAmount) {

    // total A
    BigDecimal requestedNetCostOfCounsel = toBigDecimal(feeCalculationRequest.getNetCostOfCounsel());
    BigDecimal requestedNetProfitCosts = toBigDecimal(feeCalculationRequest.getNetProfitCosts());
    BigDecimal totalA = requestedNetProfitCosts.add(requestedNetCostOfCounsel);

    // Total B
    BigDecimal escapeCaseThreshold = feeEntity.getEscapeThresholdLimit();
    BigDecimal multipliedFixedFee = fixedFeeAmount.multiply(BigDecimal.valueOf(3));
    BigDecimal requestedBoltOnValue = toBigDecimal(boltOnFeeDetails.getBoltOnTotalFeeAmount());
    BigDecimal totalB = multipliedFixedFee.add(escapeCaseThreshold).add(requestedBoltOnValue);


    if (totalA.compareTo(totalB) > 0) {
      if(totalA.compareTo(escapeCaseThreshold) <= 0) {
        return totalAmount;
      }
      if(totalA.compareTo(escapeCaseThreshold) > 0) {

      }

    }




    return requestedNetCostOfCounsel;
  }

}
