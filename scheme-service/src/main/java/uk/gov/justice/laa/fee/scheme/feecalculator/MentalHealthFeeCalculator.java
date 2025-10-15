package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isFixedFee;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.MentalHealthDisbursementCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MentalHealthFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Implementation class for Mental Health fee category (Fixed and Disbursement both).
 */
@RequiredArgsConstructor
@Component
public class MentalHealthFeeCalculator implements FeeCalculator {

  private final MentalHealthFixedFeeCalculator mentalHealthFixedFeeCalculator;

  private final MentalHealthDisbursementCalculator mentalHealthDisbursementCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.MENTAL_HEALTH);
  }

  /**
   * Determines the calculation based on Mental Health fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    if (isFixedFee(feeEntity.getFeeType())) {
      return mentalHealthFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    } else {
      return mentalHealthDisbursementCalculator.calculate(feeCalculationRequest, feeEntity);
    }
  }

}
