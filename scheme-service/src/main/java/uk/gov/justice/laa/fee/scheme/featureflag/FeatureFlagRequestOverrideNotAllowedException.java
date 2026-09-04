package uk.gov.justice.laa.fee.scheme.featureflag;

/**
 * Thrown when a request attempts to override feature flags where overrides are disabled.
 */
public class FeatureFlagRequestOverrideNotAllowedException extends RuntimeException {

  /**
   * Creates the exception.
   */
  public FeatureFlagRequestOverrideNotAllowedException() {
    super("Feature flag request overrides are not enabled");
  }
}
