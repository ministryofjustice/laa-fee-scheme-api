package uk.gov.justice.laa.fee.scheme.featureflag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides access to configured feature flags.
 */
@RequiredArgsConstructor
@Service
public class FeatureFlagService {

  private final FeatureFlagProperties featureFlagProperties;

  /**
   * Check whether a given feature is enabled.
   *
   * @param featureName the name of the feature
   * @return true when the feature is configured as enabled, otherwise false
   */
  public boolean isEnabled(String featureName) {
    if (featureName == null) {
      return false;
    }
    return Boolean.TRUE.equals(featureFlagProperties.getFlags().get(featureName));
  }
}
