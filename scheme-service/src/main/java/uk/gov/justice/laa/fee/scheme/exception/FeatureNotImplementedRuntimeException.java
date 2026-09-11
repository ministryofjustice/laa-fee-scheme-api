package uk.gov.justice.laa.fee.scheme.exception;

import uk.gov.justice.laa.fee.scheme.config.features.Feature;

/** Exception thrown when application code references an unimplemented feature. */
public class FeatureNotImplementedRuntimeException extends RuntimeException {

  public FeatureNotImplementedRuntimeException(Feature feature) {
    super("Feature has not been implemented: %s".formatted(feature));
  }
}
