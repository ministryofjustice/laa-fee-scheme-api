package uk.gov.justice.laa.fee.scheme.featureflag;

import lombok.RequiredArgsConstructor;

/**
 * Evaluates feature flags from application configuration.
 */
@RequiredArgsConstructor
public class ConfigurationFeatureFlagService implements FeatureFlagService {

  private final FeatureFlagProperties properties;

  @Override
  public boolean isEnabled(FeatureFlag featureFlag) {
    return properties.isEnabled(featureFlag);
  }
}
