package uk.gov.justice.laa.fee.scheme.controller;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).setSerializationInclusion(JsonInclude.Include.ALWAYS);

  @Override
  public ResponseEntity<FeeCalculationResponse> getFeeCalculation(FeeCalculationRequest feeCalculationRequest) {
    try {
      setUpMdc(feeCalculationRequest);

      logFeeRequest(feeCalculationRequest);
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

    if (nonNull(feeCalculationRequest.getStartDate())) {
      MDC.put("startDate", feeCalculationRequest.getStartDate().toString());
    }

    if (nonNull(feeCalculationRequest.getPoliceStationId())) {
      MDC.put("policeStationId", feeCalculationRequest.getPoliceStationId());
    }

    if (nonNull(feeCalculationRequest.getPoliceStationSchemeId())) {
      MDC.put("policeStationSchemeId", feeCalculationRequest.getPoliceStationSchemeId());
    }

    if (nonNull(feeCalculationRequest.getUniqueFileNumber())) {
      MDC.put("uniqueFileNumber", feeCalculationRequest.getUniqueFileNumber());
    }
  }

  private void logFeeRequest(FeeCalculationRequest request) {
    try {
      log.info("FeeCalculation request received: {}", objectMapper.writeValueAsString(request));
    } catch (JsonProcessingException e) {
      log.warn("FeeCalculation request received, but could not serialize object", e);
    }
  }
}