package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exception.FeeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;
import uk.gov.justice.laa.fee.scheme.repository.FeeSchemesRepository;

/**
 * Service for retrieving Database Table entities.
 */
@RequiredArgsConstructor
@Service
public class DataService {

  private final FeeRepository feeRepository;

  private final FeeSchemesRepository feeSchemesRepository;


  /**
   * Returns FeeEntity after making calls to database.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return FeeEntity
   */
  public FeeEntity getFeeEntity(FeeCalculationRequest feeCalculationRequest) {
    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    return feeRepository.findByFeeCodeAndFeeSchemeCode(feeCalculationRequest.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

  }
}
