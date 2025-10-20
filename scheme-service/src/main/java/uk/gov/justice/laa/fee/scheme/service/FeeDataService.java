package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

/**
 * Service for retrieving Database Table entities.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDataService {

  private final FeeRepository feeRepository;
  private final ValidationService validationService;

  /**
   * Returns FeeEntity after making calls to database.
   *
   * @param feeCalculationRequest FeeCalculationRequest
   * @return FeeEntity
   */
  public FeeEntity getFeeEntity(FeeCalculationRequest feeCalculationRequest) {

    log.info("Getting fee entity");

    String feeCode = feeCalculationRequest.getFeeCode();

    List<FeeEntity> feeEntityList = feeRepository.findByFeeCode(feeCode);

    FeeEntity validFeeEntity = validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest);

    log.info("Retrieved fee entity for feeCode: {}", feeCode);

    return validFeeEntity;
  }

}
