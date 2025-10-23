package uk.gov.justice.laa.fee.scheme.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.repository.FeeRepository;

/**
 * Service for retrieving Database Table entities.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDataService {

  private final FeeRepository feeRepository;

  /**
   * Returns list of fee entities for the given fee code.
   *
   * @param feeCode     the fee code
   * @return FeeEntity
   */
  public List<FeeEntity> getFeeEntities(String feeCode) {

    log.info("Getting fee entities");

    List<FeeEntity> feeEntityList = feeRepository.findByFeeCode(feeCode);

    log.info("Retrieved fee entities for feeCode: {}", feeCode);

    return feeEntityList;
  }

}
