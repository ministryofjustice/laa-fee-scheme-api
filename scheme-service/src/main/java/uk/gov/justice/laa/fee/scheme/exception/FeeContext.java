package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

/**
 * Context for fee.
 */
public record FeeContext(String feeCode, LocalDate startDate, String claimId) {
  public FeeContext(FeeCalculationRequest feeCalculationRequest) {
    this(feeCalculationRequest.getFeeCode(), feeCalculationRequest.getStartDate(), feeCalculationRequest.getClaimId());
  }
}
