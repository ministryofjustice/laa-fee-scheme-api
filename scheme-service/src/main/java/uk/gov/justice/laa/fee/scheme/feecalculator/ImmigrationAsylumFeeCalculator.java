package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isFixedFee;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.ImmigrationAsylumFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.ImmigrationAsylumHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Implementation class for Immigration Asylum fee category.
 */
@RequiredArgsConstructor
@Component
public class ImmigrationAsylumFeeCalculator implements FeeCalculator {

  private final ImmigrationAsylumFixedFeeCalculator immigrationAsylumFixedFeeCalculator;

  private final ImmigrationAsylumHourlyRateCalculator immigrationAsylumHourlyRateCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(IMMIGRATION_ASYLUM);
  }

  /**
   * Determines the calculation based on immigration asylum fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    if (isFixedFee(feeEntity.getFeeType().name())) {
      return immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    } else {
      return immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
    }
  }

}
