package uk.gov.justice.laa.fee.scheme.config;

import static java.lang.Boolean.TRUE;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotImplementedRuntimeException;

@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {

    @NotNull private Boolean isFeatureEnabled;

    private void checkFeatureEnabled(Feature feature) {
        if (!TRUE.equals(isFeatureEnabled)) {
            throw new FeatureNotEnabledException(feature);
        }
    }

    public void checkEnabled(Feature... features) {
        for (var feature : features) {
            switch (feature) {
                case FEATURE -> checkFeatureEnabled(feature);
                default -> throw new FeatureNotImplementedRuntimeException(feature);
            }
        }
    }
}
