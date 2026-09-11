package uk.gov.justice.laa.fee.scheme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;

/**
 * Annotation to indicate that a feature flag is required for a controller or method. The {@link
 * uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagInterceptor} handles feature flag checks
 * and throws a {@link uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException} if not
 * enabled.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatureFlag {
  /** The feature flags required for the annotated controller or method. */
  Feature[] value();
}
