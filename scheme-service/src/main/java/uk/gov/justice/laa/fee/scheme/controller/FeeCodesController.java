package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeCodesApi;
import uk.gov.justice.laa.fee.scheme.model.FeeCodesResponseV1;
import uk.gov.justice.laa.fee.scheme.service.FeeCodesService;

/** Controller for getting fee codes and their details corresponding to area of law. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCodesController implements FeeCodesApi {

  private final FeeCodesService feeCodesService;

  @Override
  public ResponseEntity<FeeCodesResponseV1> getFeeCodesV1(String areaOfLaw) {
    log.info("Getting fee codes (v1)");
    FeeCodesResponseV1 feeCodesResponseV1 = feeCodesService.getFeeCodesV1(areaOfLaw);
    log.info("Successfully retrieved fee codes (v1)");

    return ResponseEntity.ok(feeCodesResponseV1);
  }
}
