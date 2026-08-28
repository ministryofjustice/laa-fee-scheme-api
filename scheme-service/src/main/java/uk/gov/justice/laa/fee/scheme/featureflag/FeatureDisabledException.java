package uk.gov.justice.laa.fee.scheme.featureflag;

/**
 * Raised when an endpoint requires a disabled feature.
 */
public class FeatureDisabledException extends RuntimeException {

  /**
   * Creates an exception for the disabled feature.
   *
   * @param featureFlag the disabled feature
   */
  public FeatureDisabledException(FeatureFlag featureFlag) {
    super("Feature is not available: " + featureFlag.key());
  }
}
