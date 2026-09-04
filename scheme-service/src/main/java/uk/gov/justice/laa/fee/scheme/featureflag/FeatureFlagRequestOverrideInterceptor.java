package uk.gov.justice.laa.fee.scheme.featureflag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Validates and stores per-request feature flag overrides used by non-production tests.
 */
public class FeatureFlagRequestOverrideInterceptor implements HandlerInterceptor {

  static final String QUERY_PARAMETER = "featureFlag";

  private final FeatureFlagProperties properties;

  /**
   * Creates the interceptor.
   *
   * @param properties feature flag configuration
   */
  public FeatureFlagRequestOverrideInterceptor(FeatureFlagProperties properties) {
    this.properties = properties;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String[] configuredOverrides = request.getParameterValues(QUERY_PARAMETER);
    if (configuredOverrides == null) {
      return true;
    }

    if (!properties.requestOverridesEnabled()) {
      throw new FeatureFlagRequestOverrideNotAllowedException();
    }

    Map<FeatureFlag, Boolean> overrides = new EnumMap<>(FeatureFlag.class);
    for (String configuredOverride : configuredOverrides) {
      FeatureFlagOverride override = parse(configuredOverride);
      if (overrides.putIfAbsent(override.featureFlag(), override.enabled()) != null) {
        throw new InvalidFeatureFlagRequestOverrideException(
            "Duplicate feature flag request override: " + override.featureFlag().key());
      }
    }

    request.setAttribute(
        FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(overrides));
    return true;
  }

  private FeatureFlagOverride parse(String configuredOverride) {
    String[] parts = configuredOverride.split(":", -1);
    if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
      throw invalidFormat(configuredOverride);
    }

    FeatureFlag featureFlag = FeatureFlag.findByKey(parts[0])
        .orElseThrow(() -> new InvalidFeatureFlagRequestOverrideException(
            "Unknown feature flag request override: " + parts[0]));

    boolean enabled = switch (parts[1].toLowerCase(Locale.ROOT)) {
      case "true" -> true;
      case "false" -> false;
      default -> throw invalidFormat(configuredOverride);
    };

    return new FeatureFlagOverride(featureFlag, enabled);
  }

  private InvalidFeatureFlagRequestOverrideException invalidFormat(String configuredOverride) {
    return new InvalidFeatureFlagRequestOverrideException(
        "Invalid feature flag request override '" + configuredOverride
            + "'. Expected <flag-key>:<true|false>");
  }

  private record FeatureFlagOverride(FeatureFlag featureFlag, boolean enabled) {
  }
}
