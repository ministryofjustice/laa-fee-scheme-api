package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.exception.CategoryCodeNotFoundException;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.repository.CategoryOfLawLookUpRepository;

/**
 * Service for retrieving category of law based on fee code.
 */
@RequiredArgsConstructor
@Service
public class CategoryOfLawService {

  private final CategoryOfLawLookUpRepository repository;

  /**
   * Get a category of law code based on given fee code.
   *
   * @param feeCode the fee code
   * @return category of law response
   * @exception CategoryCodeNotFoundException category law not found
   */
  public CategoryOfLawResponse getCategoryCode(String feeCode) {

    return repository.findByFeeCode(feeCode)
        .map(entity -> CategoryOfLawResponse.builder()
            .categoryOfLawCode(entity.getCategoryCode())
            .build())
        .orElseThrow(() -> new CategoryCodeNotFoundException(feeCode));
  }
}
