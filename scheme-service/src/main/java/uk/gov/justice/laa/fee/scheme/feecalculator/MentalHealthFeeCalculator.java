package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.MentalHealthDisbursementOnlyCalculator;
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

  private final MentalHealthDisbursementOnlyCalculator mentalHealthDisbursementOnlyCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.MENTAL_HEALTH);
  }

  /**
   * Determines the calculation based on Mental Health fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    return switch (feeEntity.getFeeType()) {
      case FeeType.FIXED -> mentalHealthFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
      case FeeType.HOURLY -> throw new UnsupportedOperationException("Hourly rate fee is not supported for Mental Health category.");
      case FeeType.DISB_ONLY -> mentalHealthDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity);
    };

  }

}
