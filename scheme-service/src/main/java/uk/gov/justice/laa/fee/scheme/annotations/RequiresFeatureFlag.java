package uk.gov.justice.laa.fee.scheme.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;

/** Requires the specified feature flags for a controller or method. */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatureFlag {
  /** The feature flags required for the annotated controller or method. */
  Feature[] value();
}
