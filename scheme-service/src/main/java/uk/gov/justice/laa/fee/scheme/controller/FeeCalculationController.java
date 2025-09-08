package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.api.FeeCalculationApi;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeCalculatorService;
import uk.gov.justice.laa.fee.scheme.service.FeeService;

/**
 * Controller for post fee data, to be used in fee calculation.
 */
@RestController
@RequiredArgsConstructor
public class FeeCalculationController implements FeeCalculationApi {

  private final FeeCalculatorService feeCalculatorService;

  @Override
  public ResponseEntity<FeeCalculationResponse> getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {

    FeeCalculationResponse feeCalculationResponse = feeCalculatorService.calculateFee(feeCalculationRequest);

    return ResponseEntity.ok(feeCalculationResponse);
  }
}