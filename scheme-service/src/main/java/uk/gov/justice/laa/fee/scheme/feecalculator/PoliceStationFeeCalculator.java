package uk.gov.justice.laa.fee.scheme.feecalculator;

import static uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType.POLICE_STATION;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.DateUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculator;

/**
 * Implementation class for police station fixed fee category.
 */
@RequiredArgsConstructor
@Component
public class PoliceStationFeeCalculator implements FeeCalculator {

  private static final String INVC = "INVC";

  private final FeeRepository feeRepository;

  private final FeeSchemesRepository feeSchemesRepository;

  private final PoliceStationFixedFeeCalculator fixedFeeStrategy;

  private final PoliceStationHourlyFeeCalculator hourlyFeeStrategy;

  @Override
  public CategoryType getCategory() {
    return POLICE_STATION;
  }

  private boolean isFixedFee(FeeEntity feeEntity) {
    return feeEntity.getFeeType().name().equals("FIXED");
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

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode(feeCalculationRequest.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));


    if (isFixedFee(feeEntity)) {
      return fixedFeeStrategy.getFee(feeEntity,feeCalculationRequest);
    } else {
      return hourlyFeeStrategy.getFee(feeEntity,feeCalculationRequest);
    }
  }

}
