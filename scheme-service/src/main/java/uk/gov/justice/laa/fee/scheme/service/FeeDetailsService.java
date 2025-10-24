package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.CaseType;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeCategoryMappingRepository;

/**
 * Service for retrieving category of law and Fee details based on fee code.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FeeDetailsService {

  private final FeeCategoryMappingRepository feeCategoryMappingRepository;

  /**
   * Get a category of law code based on given fee code.
   * Get Fee code type and fee code description based on given fee code.
   *
   * @param feeCode the fee code
   * @return category of law response
   * @throws CategoryCodeNotFoundException category law not found
   */
  public FeeDetailsResponse getFeeDetails(String feeCode) {

    log.info("Get category of law and fee details");

    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCode);

    return FeeDetailsResponse.builder()
        .categoryOfLawCode(feeCategoryMapping.getCategoryOfLawType().getCode())
        .feeCodeDescription(feeCategoryMapping.getFeeDescription())
        .feeType(feeCategoryMapping.getFeeType().name())
        .build();
  }

  /**
   * Get case type based on given fee code.
   *
   * @param feeCode the fee code
   * @return the case ty[e
   * @throws CategoryCodeNotFoundException category law not found
   */
  public CaseType getCaseType(String feeCode) {
    FeeCategoryMappingEntity feeCategoryMapping = getFeeCategoryMapping(feeCode);

    return feeCategoryMapping.getCategoryOfLawType().getAreaOfLawType().getCaseType();
  }

  private FeeCategoryMappingEntity getFeeCategoryMapping(String feeCode) {
    return feeCategoryMappingRepository.findFeeCategoryMappingByFeeCode(feeCode)
        .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
