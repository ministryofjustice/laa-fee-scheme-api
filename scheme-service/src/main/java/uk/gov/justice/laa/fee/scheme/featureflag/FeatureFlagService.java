package uk.gov.justice.laa.fee.scheme.featureflag;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Evaluates feature flags from application configuration.
 */
@RequiredArgsConstructor
public class FeatureFlagService {

  private final FeatureFlagProperties properties;

  /**
   * Checks whether a feature is enabled.
   *
   * @param featureFlag the feature to evaluate
   * @return true when the feature is enabled
   */
  public boolean isEnabled(FeatureFlag featureFlag) {
    return getRequestOverride(featureFlag).orElseGet(() -> properties.isEnabled(featureFlag));
  }

  private Optional<Boolean> getRequestOverride(FeatureFlag featureFlag) {
    if (!properties.requestOverridesEnabled()
        || !(RequestContextHolder.getRequestAttributes()
            instanceof ServletRequestAttributes requestAttributes)) {
      return Optional.empty();
    }

    HttpServletRequest request = requestAttributes.getRequest();
    Object requestOverrides = request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE);
    if (!(requestOverrides instanceof FeatureFlagRequestOverrides overrides)) {
      return Optional.empty();
    }

    return overrides.get(featureFlag);
  }
}
