package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeDetailsApi;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

/**
 * Controller for getting category of law code and Fee details corresponding to fee code.
 */
@RestController
@RequiredArgsConstructor
public class FeeDetailsController implements FeeDetailsApi {

  private final FeeDetailsService feeDetailsService;

  @Override
  public ResponseEntity<FeeDetailsResponse> getFeeDetails(@PathVariable String feeCode) {

    FeeDetailsResponse feeDetails = feeDetailsService.getFeeDetails(feeCode);

    return ResponseEntity.ok(feeDetails);
  }

}
