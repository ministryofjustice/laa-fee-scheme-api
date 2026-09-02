package uk.gov.justice.laa.fee.scheme.config;

import static java.lang.Boolean.TRUE;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotImplementedRuntimeException;

/** Configuration properties for feature flags. */
@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags")
public class FeatureFlagsConfig {

  @NotNull private Boolean isFeatureEnabled;

  /**
   * Checks if the specified feature flag is enabled. If the feature is not enabled, a {@link
   * FeatureNotEnabledException} is thrown.
   *
   * @param feature the feature flag to check
   */
  private void checkFeatureEnabled(Feature feature) {
    if (!TRUE.equals(isFeatureEnabled)) {
      throw new FeatureNotEnabledException(feature);
    }
  }

  /**
   * Checks if the specified feature flags are enabled. If any of the features are not enabled, a
   * {@link FeatureNotEnabledException} is thrown. If a feature is not implemented, a {@link
   * FeatureNotImplementedRuntimeException} is thrown.
   *
   * @param features the feature flags to check
   */
  public void checkEnabled(Feature... features) {
    for (var feature : features) {
      switch (feature) {
        case FEATURE -> checkFeatureEnabled(feature);
        default -> throw new FeatureNotImplementedRuntimeException(feature);
      }
    }
  }
}
