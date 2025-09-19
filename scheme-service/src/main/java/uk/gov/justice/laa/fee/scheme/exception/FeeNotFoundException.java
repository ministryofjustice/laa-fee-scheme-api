package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception for when no fee entity can be found for fee_code and start_date.
 */
public class FeeNotFoundException extends RuntimeException  {
  public FeeNotFoundException(String feeCode, LocalDate startDate) {
    super(String.format("Fee not found for feeCode: %s and startDate: %s", feeCode, startDate));
  }
}