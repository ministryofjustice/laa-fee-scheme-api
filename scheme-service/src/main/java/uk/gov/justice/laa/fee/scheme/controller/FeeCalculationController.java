package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    try {
      setUpMdc(feeCalculationRequest);

      log.info("Getting fee calculation");

      FeeCalculationResponse feeCalculationResponse = feeCalculationService.calculateFee(feeCalculationRequest);

      log.info("Successfully retrieved fee calculation");

      return ResponseEntity.ok(feeCalculationResponse);
    } finally {
      MDC.clear();
    }
  }

  private void setUpMdc(FeeCalculationRequest feeCalculationRequest) {
    MDC.put("feeCode", feeCalculationRequest.getFeeCode());

    if (feeCalculationRequest.getStartDate() != null) {
      MDC.put("startDate", feeCalculationRequest.getStartDate().toString());
    }

    if (feeCalculationRequest.getPoliceStationId() != null) {
      MDC.put("policeStationId", feeCalculationRequest.getPoliceStationId());
    }

    if (feeCalculationRequest.getPoliceStationSchemeId() != null) {
      MDC.put("policeStationSchemeId", feeCalculationRequest.getPoliceStationSchemeId());
    }

    if (feeCalculationRequest.getUniqueFileNumber() != null) {
      MDC.put("uniqueFileNumber", feeCalculationRequest.getUniqueFileNumber());
    }
  }
}