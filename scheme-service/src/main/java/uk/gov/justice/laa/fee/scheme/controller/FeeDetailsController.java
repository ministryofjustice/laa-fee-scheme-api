package uk.gov.justice.laa.fee.scheme.controller;

import static uk.gov.justice.laa.fee.scheme.util.LoggingUtil.getLogMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<FeeDetailsResponse> getFeeDetails(@PathVariable String feeCode) {
    log.info(getLogMessage("Getting fee details for fee code", feeCode));

    FeeDetailsResponse feeDetails = feeDetailsService.getFeeDetails(feeCode);

    log.info(getLogMessage("Retrieved fee details for fee code", feeCode));

    return ResponseEntity.ok(feeDetails);
  }

}
