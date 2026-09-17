package uk.gov.justice.laa.fee.scheme.config.features;

import java.util.Map;
import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Immutable overrides stored on the servlet request, never in shared configuration. */
public record FeatureFlagRequestOverrides(Map<Feature, Boolean> values) {

  static final String REQUEST_ATTRIBUTE = FeatureFlagRequestOverrides.class.getName();

  /** Copies the overrides so callers cannot change values after validation. */
  public FeatureFlagRequestOverrides {
    values = Map.copyOf(values);
  }

  /** Returns an override only when the current thread is handling a request containing one. */
  public static Optional<Boolean> getCurrent(Feature feature) {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes
        && attributes.getRequest().getAttribute(REQUEST_ATTRIBUTE)
            instanceof FeatureFlagRequestOverrides overrides) {
      return Optional.ofNullable(overrides.values().get(feature));
    }
    return Optional.empty();
  }
}
