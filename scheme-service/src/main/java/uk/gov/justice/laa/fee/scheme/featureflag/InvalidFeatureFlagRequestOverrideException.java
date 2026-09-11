package uk.gov.justice.laa.fee.scheme.featureflag;

/**
 * Thrown when a feature flag request override is invalid.
 */
public class InvalidFeatureFlagRequestOverrideException extends RuntimeException {

  /**
   * Creates the exception.
   *
   * @param message validation failure
   */
  public InvalidFeatureFlagRequestOverrideException(String message) {
    super(message);
  }
}
