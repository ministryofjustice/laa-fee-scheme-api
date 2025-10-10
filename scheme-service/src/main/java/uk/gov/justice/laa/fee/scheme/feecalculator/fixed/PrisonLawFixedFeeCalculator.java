package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatAmount;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.VatUtil.getVatRateForDate;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toBigDecimal;
import static uk.gov.justice.laa.fee.scheme.util.NumberUtil.toDouble;

import java.math.BigDecimal;
import java.time.LocalDate;
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

/**
 * Calculate the prison law fee for a given fee entity and fee data.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PrisonLawFixedFeeCalculator implements FeeCalculator {

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.PRISON_LAW);
  }

  /**
   * Determines the calculation based on prison law fee code.
   */
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    log.info("Calculate Prison Law fixed fee");

    return calculateFeesUsingFeeCode(feeEntity, feeCalculationRequest);

  }

  private FeeCalculationResponse calculateFeesUsingFeeCode(FeeEntity feeEntity,
                                                           FeeCalculationRequest feeCalculationRequest) {

    log.info("Calculate fixed fee and costs using fee entity");

    FeeCalculation feeCalculation = mapFeeCalculation(feeCalculationRequest, feeEntity.getFixedFee());

    log.info("Build fee calculation response");
    return FeeCalculationResponse.builder()
        .feeCode(feeCalculationRequest.getFeeCode())
        .schemeId(feeEntity.getFeeScheme().getSchemeCode())
        .escapeCaseFlag(false) // temp hard coded, till escape logic implemented
        .feeCalculation(feeCalculation).build();
  }


  /**
   * Mapping fee elements into Fee Calculation.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @param fixedFee BigDecimal
   * @return FeeCalculation
   */
  private FeeCalculation mapFeeCalculation(FeeCalculationRequest feeCalculationRequest, BigDecimal fixedFee) {

    BigDecimal requestedNetDisbursementAmount = toBigDecimal(feeCalculationRequest.getNetDisbursementAmount());

    BigDecimal requestedDisbursementVatAmount = toBigDecimal(feeCalculationRequest.getDisbursementVatAmount());

    LocalDate claimStartDate = FeeCalculationUtil
        .getFeeClaimStartDate(CategoryType.PRISON_LAW, feeCalculationRequest);

    // Apply VAT where applicable
    Boolean vatApplicable = feeCalculationRequest.getVatIndicator();

    BigDecimal fixedFeeVatAmount = getVatAmount(fixedFee, claimStartDate, vatApplicable);

    BigDecimal totalAmount = FeeCalculationUtil.calculateTotalAmount(fixedFee, fixedFeeVatAmount,
        requestedNetDisbursementAmount, requestedDisbursementVatAmount);

    return FeeCalculation.builder()
        .totalAmount(toDouble(totalAmount))
        .vatIndicator(vatApplicable)
        .vatRateApplied(toDouble(getVatRateForDate(claimStartDate)))
        .calculatedVatAmount(toDouble(fixedFeeVatAmount))
        .disbursementAmount(toDouble(requestedNetDisbursementAmount))
        .requestedNetDisbursementAmount(toDouble(requestedNetDisbursementAmount))
        .disbursementVatAmount(toDouble(requestedDisbursementVatAmount))
        .fixedFeeAmount(toDouble(fixedFee))
        .build();
  }

}