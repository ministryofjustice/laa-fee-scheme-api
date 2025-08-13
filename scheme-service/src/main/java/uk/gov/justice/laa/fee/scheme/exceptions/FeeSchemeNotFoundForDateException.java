package uk.gov.justice.laa.fee.scheme.exceptions;

import java.time.LocalDate;

/**
 * Exception for when no valid fee scheme found for fee_code and start date.
 */
public class FeeSchemeNotFoundForDateException extends RuntimeException  {
  public FeeSchemeNotFoundForDateException(String feeCode, LocalDate date) {
    super(String.format("No fee scheme found for fee %s, with date %s", feeCode, date));
  }
}