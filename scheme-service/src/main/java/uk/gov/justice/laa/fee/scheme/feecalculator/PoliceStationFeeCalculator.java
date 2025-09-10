package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.utility.FeeCalculationUtility.isFixedFee;

import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.util.DateUtility;

/**
 * Implementation class for police station fixed fee category.
 */
@RequiredArgsConstructor
@Component
public class PoliceStationFeeCalculator implements FeeCalculator {

  private static final String INVC = "INVC";

  private final DataService dataService;

  private final PoliceStationFixedFeeCalculator policeStationFixedFeeCalculator;

  private final PoliceStationHourlyFeeCalculator policeStationHourlyFeeCalculator;

  @Override
  public Set<CategoryType> getSupportedCategories() {
    return Set.of(CategoryType.POLICE_STATION);
  }

  /**
   * Determines the calculation based on police fee code.
   */
  @Override
  public FeeCalculationResponse calculate(FeeCalculationRequest feeCalculationRequest) {

    if (StringUtils.isNotBlank(feeCalculationRequest.getUniqueFileNumber())) {
      LocalDate caseStartDate = DateUtility.toLocalDate(feeCalculationRequest.getUniqueFileNumber());
      feeCalculationRequest.setStartDate(caseStartDate);
    }

    FeeEntity feeEntity = dataService.getFeeEntity(feeCalculationRequest);

    if (isFixedFee(feeEntity.getFeeType().name())) {
      return policeStationFixedFeeCalculator.getFee(feeEntity, feeCalculationRequest);
    } else {
      return policeStationHourlyFeeCalculator.getFee(feeEntity, feeCalculationRequest);
    }
  }

}
