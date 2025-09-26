package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil.isFixedFee;

import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.util.DateUtil;

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

    if (StringUtils.isNotBlank(feeCalculationRequest.getUniqueFileNumber())) {
      LocalDate caseStartDate = DateUtil.toLocalDate(feeCalculationRequest.getUniqueFileNumber());
      feeCalculationRequest.setStartDate(caseStartDate);
    }

    if (isFixedFee(feeEntity.getFeeType())) {
      return policeStationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);
    } else {
      return policeStationHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
    }
  }

}
