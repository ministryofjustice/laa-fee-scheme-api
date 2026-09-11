package uk.gov.justice.laa.fee.scheme.exception;

/** Exception thrown when request overrides are not permitted in the current environment. */
public class FeatureFlagRequestOverrideNotAllowedException extends RuntimeException {

  public FeatureFlagRequestOverrideNotAllowedException() {
    super("Feature flag request overrides are not allowed");
  }
}
