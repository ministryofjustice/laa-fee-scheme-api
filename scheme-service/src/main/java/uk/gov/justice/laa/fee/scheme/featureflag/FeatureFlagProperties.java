package uk.gov.justice.laa.fee.scheme.featureflag;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration-backed feature flag values.
 *
 * @param flags configured flag values keyed by {@link FeatureFlag#key()}
 * @param requestOverridesEnabled whether REST requests may override flags for testing
 */
@ConfigurationProperties("feature-flags")
public record FeatureFlagProperties(
    Map<String, Boolean> flags,
    boolean requestOverridesEnabled) {

  /**
   * Creates immutable feature flag properties and rejects unknown flag keys.
   *
   * @param flags configured flag values
   * @param requestOverridesEnabled whether REST requests may override flags for testing
   */
  public FeatureFlagProperties {
    flags = flags == null ? Map.of() : Map.copyOf(flags);

    List<String> unknownFlagKeys = flags.keySet()
        .stream()
        .filter(key -> FeatureFlag.findByKey(key).isEmpty())
        .sorted()
        .toList();

    if (!unknownFlagKeys.isEmpty()) {
      throw new IllegalArgumentException(
          "Unknown feature flag configuration: " + String.join(", ", unknownFlagKeys));
    }
  }

  /**
   * Checks the configured value, defaulting to disabled when the flag is absent.
   *
   * @param featureFlag the feature to evaluate
   * @return true when the feature is configured as enabled
   */
  public boolean isEnabled(FeatureFlag featureFlag) {
    return flags.getOrDefault(featureFlag.key(), false);
  }
}
