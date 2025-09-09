package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.ImmigrationAsylumFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.ImmigrationAsylumHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;

/**
 * Implementation class for police station fixed fee category.
 */
@RequiredArgsConstructor
@Component
public class ImmigrationAsylumFeeCalculator implements FeeCalculator {

  private static final String INVC = "INVC";

  private final DataService dataService;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(IMMIGRATION_ASYLUM);
  }

  private boolean isFixedFee(FeeEntity feeEntity) {
    return feeEntity.getFeeType().name().equals("FIXED");
  }

  /**
   * Determines the calculation based on police fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest) {

    FeeEntity feeEntity = dataService.getFeeEntity(feeCalculationRequest);

    if (isFixedFee(feeEntity)) {
      return ImmigrationAsylumFixedFeeCalculator.getFee(feeEntity,feeCalculationRequest);
    } else {
      return ImmigrationAsylumHourlyRateCalculator.getFee(feeEntity,feeCalculationRequest);
    }
  }

}
