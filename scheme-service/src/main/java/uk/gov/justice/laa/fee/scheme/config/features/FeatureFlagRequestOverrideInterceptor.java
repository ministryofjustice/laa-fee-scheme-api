package uk.gov.justice.laa.fee.scheme.config.features;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.exception.FeatureFlagRequestOverrideNotAllowedException;
import uk.gov.justice.laa.fee.scheme.exception.InvalidFeatureFlagRequestOverrideException;

/** Validates non-production test overrides from the featureFlag query parameter. */
@Component
@RequiredArgsConstructor
public class FeatureFlagRequestOverrideInterceptor implements HandlerInterceptor {

  private final FeatureFlagsConfig featureFlagsConfig;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String[] parameters = request.getParameterValues("featureFlag");
    if (parameters == null) {
      return true;
    }
    if (!featureFlagsConfig.isRequestOverridesEnabled()) {
      throw new FeatureFlagRequestOverrideNotAllowedException();
    }

    Map<Feature, Boolean> overrides = new EnumMap<>(Feature.class);
    for (String parameter : parameters) {
      String[] parts = parameter.split(":", -1);
      if (parts.length != 2) {
        throw invalidFormat(parameter);
      }
      Feature feature;
      try {
        feature = Feature.valueOf(parts[0]);
      } catch (IllegalArgumentException ex) {
        throw new InvalidFeatureFlagRequestOverrideException(
            "Unknown feature flag request override: " + parts[0]);
      }
      boolean enabled = switch (parts[1].toLowerCase(Locale.ROOT)) {
        case "true" -> true;
        case "false" -> false;
        default -> throw invalidFormat(parameter);
      };
      if (overrides.putIfAbsent(feature, enabled) != null) {
        throw new InvalidFeatureFlagRequestOverrideException(
            "Duplicate feature flag request override: " + feature);
      }
    }
    request.setAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(overrides));
    return true;
  }

  private InvalidFeatureFlagRequestOverrideException invalidFormat(String parameter) {
    return new InvalidFeatureFlagRequestOverrideException(
        "Invalid feature flag request override '" + parameter
            + "'. Expected <FEATURE_ENUM_NAME>:<true|false>");
  }
}
