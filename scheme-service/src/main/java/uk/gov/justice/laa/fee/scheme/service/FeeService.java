package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exceptions.FeeEntityNotFoundException;
import uk.gov.justice.laa.fee.scheme.exceptions.FeeSchemeNotFoundForDateException;
import uk.gov.justice.laa.fee.scheme.feecalculators.CalculateMediationFee;
import uk.gov.justice.laa.fee.scheme.feecalculators.CalculationType;
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

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository.findValidSchemeForDate(feeData.getFeeCode(), feeData.getStartDate())
        .orElseThrow(() -> new FeeSchemeNotFoundForDateException(feeData.getFeeCode(), feeData.getStartDate()));
    String schemeId = feeSchemesEntity.getSchemeCode();

    FeeEntity feeEntity = feeRepository.findByFeeCodeAndFeeSchemeCode_SchemeCode(feeData.getFeeCode(), schemeId)
        .orElseThrow(() -> new FeeEntityNotFoundException(feeData.getFeeCode(), schemeId));

    CalculationType calculationType = feeEntity.getCalculationType();

    return getCalculation(calculationType, feeEntity, feeData);
  }

  /**
   * Perform calculation based on calculation type.
   */
  public FeeCalculationResponse getCalculation(CalculationType calculationType, FeeEntity feeEntity,
                                               FeeCalculationRequest feeData) {

    return switch (calculationType) {
      case MEDIATION -> CalculateMediationFee.getFee(feeEntity, feeData);
      default -> null;
    };
  }
}

