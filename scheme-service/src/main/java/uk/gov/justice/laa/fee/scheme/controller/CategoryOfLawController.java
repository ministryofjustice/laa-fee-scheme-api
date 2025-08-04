package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.CategoryOfLawApi;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.service.CategoryOfLawService;

/**
 * Controller for getting category of law code corresponding to fee code.
 */
@RestController
@RequiredArgsConstructor
public class CategoryOfLawController implements CategoryOfLawApi {

  private final CategoryOfLawService categoryOfLawService;

  @Override
  public ResponseEntity<CategoryOfLawResponse> getCategoryOfLaw(@PathVariable String feeCode) {

    CategoryOfLawResponse categoryOfLawResponse = categoryOfLawService.getCategoryCode(feeCode);

    return ResponseEntity.ok(categoryOfLawResponse);
  }

}
