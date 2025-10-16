package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.ImmigrationAsylumDisbursementOnlyCalculator;
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
  private final ImmigrationAsylumDisbursementOnlyCalculator immigrationAsylumDisbursementOnlyCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(IMMIGRATION_ASYLUM);
  }

  /**
   * Determines the calculation based on immigration asylum fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    return switch (feeEntity.getFeeType()) {
      case FIXED -> immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
      case HOURLY -> immigrationAsylumHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
      case DISB_ONLY -> immigrationAsylumDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity);
    };

  }

}
