package uk.gov.justice.laa.fee.scheme.exception;

/**
 * Exception when Start Date is not provided.
 */
public class StartDateRequiredException extends RuntimeException {
  public StartDateRequiredException(String feeCode) {
    super(String.format("Start Date is required for feeCode: %s", feeCode));
  }
}