package uk.gov.justice.laa.fee.scheme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;

/**
 * Service for retrieving category of law based on fee code.
 */
@RequiredArgsConstructor
@Service
public class CategoryOfLawService {

  /**
   * Geta category of law code based on given fee code.
   *
   * @param feeCode the fee code
   * @return category of law response
   */
  public CategoryOfLawResponse getCategoryCode(String feeCode) {
    return CategoryOfLawResponse.builder()
        .categoryOfLawCode("ASY")
        .build();
  }
}
