package uk.gov.justice.laa.fee.scheme.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagRequestOverrides;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotImplementedRuntimeException;

/** Configuration properties and request-aware evaluation for feature flags. */
@Data
@Configuration
@ConfigurationProperties(prefix = "feature-flags", ignoreUnknownFields = false)
@Validated
public class FeatureFlagsConfig {

  @NotNull private Boolean isFeatureEnabled;

  private boolean requestOverridesEnabled;

  /** Returns the effective flag value for inline checks, including a request override. */
  public Boolean getIsFeatureEnabled() {
    return isEnabled(Feature.FEATURE);
  }

  /** Returns whether a feature is enabled for the current request or background operation. */
  public boolean isEnabled(Feature feature) {
    if (feature == null) {
      throw new FeatureNotImplementedRuntimeException(null);
    }
    Boolean configuredValue = switch (feature) {
      case FEATURE -> isFeatureEnabled;
      default -> throw new FeatureNotImplementedRuntimeException(feature);
    };
    if (configuredValue == null) {
      throw new IllegalStateException("Missing feature flag configuration: " + feature);
    }
    return requestOverridesEnabled
        ? FeatureFlagRequestOverrides.getCurrent(feature).orElse(configuredValue)
        : configuredValue;
  }

  /** Requires all specified features to be enabled. */
  public void checkEnabled(Feature... features) {
    for (Feature feature : features) {
      if (!isEnabled(feature)) {
        throw new FeatureNotEnabledException(feature);
      }
    }
  }
}
