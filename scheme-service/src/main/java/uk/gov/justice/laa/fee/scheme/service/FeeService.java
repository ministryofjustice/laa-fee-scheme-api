package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAndAsylumFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.MediationFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.OtherCivilFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;

/**
 *  Service for determining fee calculation for a fee code and fee schema.
 */
@RequiredArgsConstructor
@Service
public class FeeService {

  private final FeeRepository feeRepository;
  private final FeeSchemesRepository feeSchemesRepository;

  /**
   * Get fee entity for a fee schema for a given date.
   * get calculation based on calculation type.
   */
  public FeeCalculationResponse getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode(feeCalculationRequest.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    return getCalculation(feeEntity, feeCalculationRequest);
  }

  /**
   * Perform calculation based on calculation type.
   */
  private FeeCalculationResponse getCalculation(FeeEntity feeEntity, FeeCalculationRequest feeCalculationRequest) {

    return switch (feeEntity.getCalculationType()) {
      case COMMUNITY_CARE -> OtherCivilFeeCalculator.getFee(feeEntity, feeCalculationRequest);
      case MEDIATION -> MediationFeeCalculator.getFee(feeEntity, feeCalculationRequest);
      case IMMIGRATION_ASYLUM_FIXED_FEE -> ImmigrationAndAsylumFeeCalculator.getFee(feeEntity, feeCalculationRequest);
    };
  }
}

