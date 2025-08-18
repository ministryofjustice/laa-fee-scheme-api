package uk.gov.justice.laa.fee.scheme.exception;

import java.time.LocalDate;

/**
 * Exception for when no fee entity can be found for fee_code and fee_scheme_code.
 */
public class FeeNotFoundException extends RuntimeException  {
  public FeeNotFoundException(String feeCode, LocalDate startDate) {
    super(String.format("Fee not found for fee code %s, with start date %s", feeCode, startDate));
  }
}