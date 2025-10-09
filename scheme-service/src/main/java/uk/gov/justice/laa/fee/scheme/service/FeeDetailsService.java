package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
   * @exception CategoryCodeNotFoundException category law not found
   */
  public FeeDetailsResponse getFeeDetails(String feeCode) {

    log.info("Get category of law and fee details");

    return feeCategoryMappingRepository.findFeeCategoryMappingByFeeCode(feeCode)
        .map(feeCategoryMapping -> FeeDetailsResponse.builder()
            .categoryOfLawCode(feeCategoryMapping.getCategoryOfLawType().getCode())
            .feeCodeDescription(feeCategoryMapping.getFeeDescription())
            .feeType(feeCategoryMapping.getFeeType().name())
            .build())
          .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
