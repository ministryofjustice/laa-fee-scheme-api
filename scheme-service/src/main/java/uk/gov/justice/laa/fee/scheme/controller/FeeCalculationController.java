package uk.gov.justice.laa.fee.scheme.controller;

import static uk.gov.justice.laa.fee.scheme.util.LoggingUtil.getLogMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeCalculationApi;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculationService;

/**
 * Controller for post fee data, to be used in fee calculation.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCalculationController implements FeeCalculationApi {

  private final FeeCalculationService feeCalculationService;

  @Override
  public ResponseEntity<FeeCalculationResponse> getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {
    log.info(getLogMessage("Getting fee calculation", feeCalculationRequest));

    FeeCalculationResponse feeCalculationResponse = feeCalculationService.calculateFee(feeCalculationRequest);

    log.info(getLogMessage("Retrieved fee calculation", feeCalculationRequest));

    return ResponseEntity.ok(feeCalculationResponse);
  }
}