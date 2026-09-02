package uk.gov.justice.laa.fee.scheme.exception;

import uk.gov.justice.laa.fee.scheme.config.features.Feature;

public class FeatureNotEnabledException extends RuntimeException {

    public FeatureNotEnabledException(Feature feature) {
        super("Feature is not available: %s".formatted(feature));
    }
}
