package uk.gov.justice.laa.fee.scheme.exceptions;

/**
 * Exception when no category code found for supplied fee code.
 */
public class CategoryCodeNotFound extends RuntimeException {
  public CategoryCodeNotFound(String feeCode) {
    super(String.format("Category of code not found for fee: %s", feeCode));
  }
}
