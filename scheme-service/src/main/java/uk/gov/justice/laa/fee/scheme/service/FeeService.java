package uk.gov.justice.laa.fee.scheme.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.exception.PoliceStationFeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.FixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAndAsylumFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.MediationFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.PoliceStationFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.utility.DateUtility;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;
import uk.gov.justice.laa.fee.scheme.repository.PoliceStationFeesRepository;

/**
 * Service for determining fee calculation for a fee code and fee schema.
 */
@RequiredArgsConstructor
@Service
public class FeeService {

  public static final String INVC = "INVC";
  private final FeeRepository feeRepository;
  private final FeeSchemesRepository feeSchemesRepository;
  private final PoliceStationFeesRepository policeStationFeesRepository;

  /**
   * Get fee entity for a fee schema for a given date.
   * get calculation based on calculation type.
   */
  public FeeCalculationResponse getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {


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

    PoliceStationFeesEntity policeStationFeesEntity = getPoliceStationFeesEntity(feeCalculationRequest, feeSchemesEntity);

    return getCalculation(feeEntity, policeStationFeesEntity, feeCalculationRequest);
  }

  /**
   * Perform calculation based on calculation type.
   */
  private FeeCalculationResponse getCalculation(FeeEntity feeEntity, PoliceStationFeesEntity policeStationFeesEntity,
                                                FeeCalculationRequest feeCalculationRequest) {

    return switch (feeEntity.getCalculationType()) {
      case CLAIMS_PUBLIC_AUTHORITIES, CLINICAL_NEGLIGENCE, COMMUNITY_CARE, DEBT, HOUSING, HOUSING_HLPAS,
           MENTAL_HEALTH, MISCELLANEOUS, PUBLIC_LAW -> FixedFeeCalculator.getFee(feeEntity, feeCalculationRequest);
      case IMMIGRATION_ASYLUM_FIXED_FEE -> ImmigrationAndAsylumFixedFeeCalculator.getFee(feeEntity, feeCalculationRequest);
      case MEDIATION -> MediationFeeCalculator.getFee(feeEntity, feeCalculationRequest);
      case POLICE_STATION -> PoliceStationFeeCalculator.getFee(feeEntity, policeStationFeesEntity,
              feeCalculationRequest);
    };
  }

  /**
   * Retrieving Police Station Fee using Police Station ID/ PS Scheme ID and Fee Scheme Code.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @param feeSchemesEntity FeeSchemesEntity
   * @return PoliceStationFeesEntity
   */
  private PoliceStationFeesEntity getPoliceStationFeesEntity(FeeCalculationRequest feeCalculationRequest,
                                                             FeeSchemesEntity feeSchemesEntity) {
    PoliceStationFeesEntity policeStationFeesEntity = null;
    if (feeCalculationRequest.getFeeCode().equals(INVC)) {
      if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationId())) {
        policeStationFeesEntity = policeStationFeesRepository
            .findPoliceStationFeeByPoliceStationIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationId(),
                feeSchemesEntity.getSchemeCode())
            .stream()
            .findFirst()
            .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationId(),
                feeCalculationRequest.getStartDate()));
      } else if (StringUtils.isNotBlank(feeCalculationRequest.getPoliceStationSchemeId())) {
        policeStationFeesEntity = policeStationFeesRepository
            .findPoliceStationFeeByPsSchemeIdAndFeeSchemeCode(feeCalculationRequest.getPoliceStationSchemeId(),
                feeSchemesEntity.getSchemeCode())
            .stream()
            .findFirst()
            .orElseThrow(() -> new PoliceStationFeeNotFoundException(feeCalculationRequest.getPoliceStationSchemeId()));
      }
    }
    return policeStationFeesEntity;
  }
}

