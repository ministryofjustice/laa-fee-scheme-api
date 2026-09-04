package uk.gov.justice.laa.fee.scheme.featureflag;

import lombok.RequiredArgsConstructor;

/**
 * Evaluates feature flags from application configuration.
 */
@RequiredArgsConstructor
public class FeatureFlagService {

  private final FeatureFlagProperties properties;

  /**
   * Checks whether a feature is enabled.
   *
   * @param featureFlag the feature to evaluate
   * @return true when the feature is enabled
   */
  public boolean isEnabled(FeatureFlag featureFlag) {
    return properties.isEnabled(featureFlag);
  }
}
