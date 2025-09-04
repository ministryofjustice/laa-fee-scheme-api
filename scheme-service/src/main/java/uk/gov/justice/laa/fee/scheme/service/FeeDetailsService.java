package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.repository.FeeDetailsLookUpRepository;

/**
 * Service for retrieving category of law and Fee details based on fee code.
 */
@RequiredArgsConstructor
@Service
public class FeeDetailsService {

  private final FeeDetailsLookUpRepository categoryOfLawRepository;

  /**
   * Get a category of law code based on given fee code.
   * Get Fee code type and fee code description based on given fee code.
   *
   * @param feeCode the fee code
   * @return category of law response
   * @exception CategoryCodeNotFoundException category law not found
   */
  public FeeDetailsResponse getFeeDetails(String feeCode) {

    return categoryOfLawRepository.findFeeCategoryInfoByFeeCode(feeCode)
        .map(projection -> FeeDetailsResponse.builder()
            .categoryOfLawCode(projection.getCategoryCode())
            .feeCodeDescription(projection.getDescription())
            .feeType(projection.getFeeType())
            .build())
          .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
