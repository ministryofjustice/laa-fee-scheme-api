package uk.gov.justice.laa.fee.scheme.featureflag;

import java.util.Arrays;
import java.util.Optional;

/**
 * Feature flags recognised by Fee Scheme API.
 */
public enum FeatureFlag {
  EXAMPLE_FEATURE("example-feature");

  private final String key;

  FeatureFlag(String key) {
    this.key = key;
  }

  /**
   * Gets the stable configuration key for the flag.
   *
   * @return the flag key
   */
  public String key() {
    return key;
  }

  /**
   * Finds a feature flag by its stable key.
   *
   * @param key the stable feature flag key
   * @return the matching feature flag, when recognised
   */
  public static Optional<FeatureFlag> findByKey(String key) {
    return Arrays.stream(values())
        .filter(featureFlag -> featureFlag.key.equals(key))
        .findFirst();
  }
}
