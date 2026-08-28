package uk.gov.justice.laa.fee.scheme.featureflag;

/**
 * Application-facing feature flag evaluation service.
 */
public interface FeatureFlagService {

  /**
   * Checks whether a feature is enabled.
   *
   * @param featureFlag the feature to evaluate
   * @return true when the feature is enabled
   */
  boolean isEnabled(FeatureFlag featureFlag);
}
