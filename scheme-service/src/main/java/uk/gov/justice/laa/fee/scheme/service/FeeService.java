package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exceptions.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType;
import uk.gov.justice.laa.fee.scheme.feecalculator.MediationFeeCalculator;
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
  public FeeCalculationResponse getFeeCalculation(FeeCalculationRequest feeData) {

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeData.getFeeCode(), feeData.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeData.getFeeCode(), feeData.getStartDate()));

    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode(feeData.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeData.getFeeCode(), feeData.getStartDate()));

    CalculationType calculationType = feeEntity.getCalculationType();

    return getCalculation(calculationType, feeEntity, feeData);
  }

  /**
   * Perform calculation based on calculation type.
   */
  public FeeCalculationResponse getCalculation(CalculationType calculationType, FeeEntity feeEntity,
                                               FeeCalculationRequest feeData) {

    return switch (calculationType) {
      case MEDIATION -> MediationFeeCalculator.getFee(feeEntity, feeData);
      default -> null;
    };
  }
}

