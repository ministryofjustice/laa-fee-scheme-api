package uk.gov.justice.laa.fee.scheme.exceptions;

/**
 * Exception for when no fee entity can be found for fee_code and fee_scheme_code.
 */
public class FeeEntityNotFoundException extends RuntimeException  {
  public FeeEntityNotFoundException(String feeCode, String schemeId) {
    super(String.format("Fee entity not found for fee %s, and schemeId %s", feeCode, schemeId));
  }
}