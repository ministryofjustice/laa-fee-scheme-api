package uk.gov.justice.laa.fee.scheme.service;

import static uk.gov.justice.laa.fee.scheme.util.LoggingUtil.getLogMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDataService {

  private final FeeRepository feeRepository;

  private final FeeSchemesRepository feeSchemesRepository;

  /**
   * Returns FeeEntity after making calls to database.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return FeeEntity
   */
  public FeeEntity getFeeEntity(FeeCalculationRequest feeCalculationRequest) {
    log.info(getLogMessage("Get fee entity", feeCalculationRequest));

    FeeSchemesEntity feeSchemesEntity = feeSchemesRepository
        .findValidSchemeForDate(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

    return feeRepository.findByFeeCodeAndFeeSchemeCode(feeCalculationRequest.getFeeCode(), feeSchemesEntity)
        .orElseThrow(() -> new FeeNotFoundException(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate()));

  }
}
