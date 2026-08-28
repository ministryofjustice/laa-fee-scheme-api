package uk.gov.justice.laa.fee.scheme.featureflag;

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
}
