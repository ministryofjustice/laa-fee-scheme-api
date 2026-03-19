package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeDetailsApi;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV1;
import uk.gov.justice.laa.fee.scheme.model.FeeDetailsResponseV2;
import uk.gov.justice.laa.fee.scheme.service.FeeDetailsService;

/**
 * Controller for getting category of law code and fee details corresponding to fee code.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeDetailsController implements FeeDetailsApi {

  private final FeeDetailsService feeDetailsService;

  @Override
  public ResponseEntity<FeeDetailsResponseV1> getFeeDetailsV1(String feeCode) {

    log.info("Getting fee details (v1)");
    FeeDetailsResponseV1 feeDetailsV1 = feeDetailsService.getFeeDetailsV1(feeCode);
    log.info("Successfully retrieved fee details (v1)");

    return ResponseEntity.ok(feeDetailsV1);
  }

  @Override
  public ResponseEntity<FeeDetailsResponseV2> getFeeDetailsV2(String feeCode) {
    log.info("Getting fee details (v2)");
    FeeDetailsResponseV2 feeDetailsV2 = feeDetailsService.getFeeDetailsV2(feeCode);
    log.info("Successfully retrieved fee details (v2)");

    return ResponseEntity.ok(feeDetailsV2);
  }
}
