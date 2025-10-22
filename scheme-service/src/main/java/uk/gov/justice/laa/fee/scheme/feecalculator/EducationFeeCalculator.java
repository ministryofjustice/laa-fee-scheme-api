package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isFixedFee;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.EducationDisbursementOnlyCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.EducationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Implementation class for Education fee category (Fixed and Disbursement both).
 */
@RequiredArgsConstructor
@Component
public class EducationFeeCalculator implements FeeCalculator {

  private final EducationFixedFeeCalculator educationFixedFeeCalculator;

  private final EducationDisbursementOnlyCalculator educationDisbursementOnlyCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.EDUCATION);
  }

  /**
   * Determines the calculation based on Education fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    if (isFixedFee(feeEntity.getFeeType())) {
      return educationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    } else {
      return educationDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity);
    }
  }

}
