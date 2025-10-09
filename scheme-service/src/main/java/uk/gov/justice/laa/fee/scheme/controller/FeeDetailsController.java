package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeDetailsApi;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

/**
 * Controller for getting category of law code and Fee details corresponding to fee code.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeDetailsController implements FeeDetailsApi {

  private final FeeDetailsService feeDetailsService;

  @Override
  public ResponseEntity<FeeDetailsResponse> getFeeDetails(String feeCode) {
    try {
      setUpMdc(feeCode);

      log.info("Getting fee details");

      FeeDetailsResponse feeDetails = feeDetailsService.getFeeDetails(feeCode);

      log.info("Successfully retrieved fee details");

      return ResponseEntity.ok(feeDetails);
    } finally {
      MDC.clear();
    }
  }

  private void setUpMdc(String feeCode) {
    MDC.put("feeCode", feeCode);
  }
}
