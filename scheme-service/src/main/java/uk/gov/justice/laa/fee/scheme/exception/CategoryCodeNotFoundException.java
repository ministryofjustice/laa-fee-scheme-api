package uk.gov.justice.laa.fee.scheme.exception;

/**
 * Exception when no category code found for supplied fee code.
 */
public class CategoryCodeNotFoundException extends RuntimeException {
  public CategoryCodeNotFoundException(String feeCode) {
    super(String.format("Category of law code not found for fee code: %s", feeCode));
  }
}
