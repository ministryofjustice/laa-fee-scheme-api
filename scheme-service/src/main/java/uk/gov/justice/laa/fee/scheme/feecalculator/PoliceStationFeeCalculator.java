package uk.gov.justice.laa.fee.scheme.feecalculator;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

/**
 * Implementation class for police station fee category (Fixed and Hourly both).
 */
@RequiredArgsConstructor
@Component
public class PoliceStationFeeCalculator implements FeeCalculator {

  private final PoliceStationFixedFeeCalculator policeStationFixedFeeCalculator;

  private final PoliceStationHourlyRateCalculator policeStationHourlyRateCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.POLICE_STATION);
  }

  /**
   * Determines the calculation based on police fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest, FeeEntity feeEntity) {

    return switch (feeEntity.getFeeType()) {
      case FeeType.FIXED -> policeStationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
      case FeeType.HOURLY -> policeStationHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
      case FeeType.DISB_ONLY ->
          throw new UnsupportedOperationException("Disbursement only fee is not supported for Police Station category.");
    };

  }

}
