package uk.gov.justice.laa.fee.scheme.exception;

import uk.gov.justice.laa.fee.scheme.config.features.Feature;

/**
 * Exception thrown when a feature flag is not enabled.
 */
public class FeatureNotEnabledException extends RuntimeException {

  public FeatureNotEnabledException(Feature feature) {
    super("Feature is not available: %s".formatted(feature));
  }
}
