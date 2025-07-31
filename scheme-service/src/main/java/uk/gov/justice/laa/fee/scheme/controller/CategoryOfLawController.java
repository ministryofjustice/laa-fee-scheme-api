package uk.gov.justice.laa.fee.scheme.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.CategoryOfLawApi;
import uk.gov.justice.laa.fee.scheme.model.CategoryOfLawResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeService;

/**
 * Controller for getting category of law code corresponding to fee code.
 */
@RestController
@AllArgsConstructor
public class CategoryOfLawController implements CategoryOfLawApi {

  private final FeeService feeService;

  @Override
  public ResponseEntity<CategoryOfLawResponse> getCategoryOfLaw(@PathVariable String feeCode) {

    CategoryOfLawResponse categoryOfLawDto = feeService.getCategoryCode(feeCode);

    return ResponseEntity.ok(categoryOfLawDto);
  }

}
