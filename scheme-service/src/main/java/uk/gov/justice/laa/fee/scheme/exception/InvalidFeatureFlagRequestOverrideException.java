package uk.gov.justice.laa.fee.scheme.exception;

/** Exception thrown for malformed, unknown or duplicate request overrides. */
public class InvalidFeatureFlagRequestOverrideException extends RuntimeException {

  public InvalidFeatureFlagRequestOverrideException(String message) {
    super(message);
  }
}
