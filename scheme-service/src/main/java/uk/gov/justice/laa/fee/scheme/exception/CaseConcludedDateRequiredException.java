package uk.gov.justice.laa.fee.scheme.exception;

/**
 * Exception when Case Concluded Date is not provided.
 */
public class CaseConcludedDateRequiredException extends RuntimeException {
  public CaseConcludedDateRequiredException(String feeCode) {
    super(String.format("Case Concluded Date is required for feeCode: %s", feeCode));
  }
}
