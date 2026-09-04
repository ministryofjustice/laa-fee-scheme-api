package uk.gov.justice.laa.fee.scheme.featureflag;

import java.util.Map;
import java.util.Optional;

/**
 * Feature flag values that apply only to the current REST request.
 *
 * @param values request-scoped flag values
 */
public record FeatureFlagRequestOverrides(Map<FeatureFlag, Boolean> values) {

  static final String REQUEST_ATTRIBUTE = FeatureFlagRequestOverrides.class.getName();

  /**
   * Creates an immutable set of request overrides.
   *
   * @param values request-scoped flag values
   */
  public FeatureFlagRequestOverrides {
    values = Map.copyOf(values);
  }

  /**
   * Gets the request override for a feature flag.
   *
   * @param featureFlag the feature flag
   * @return the override when one was supplied
   */
  public Optional<Boolean> get(FeatureFlag featureFlag) {
    return Optional.ofNullable(values.get(featureFlag));
  }
}
