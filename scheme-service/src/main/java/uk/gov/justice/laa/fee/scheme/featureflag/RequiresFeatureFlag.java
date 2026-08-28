package uk.gov.justice.laa.fee.scheme.featureflag;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a feature flag to be enabled before a controller or controller method can run.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresFeatureFlag {

  /**
   * The feature required by the endpoint.
   *
   * @return the required feature
   */
  FeatureFlag value();
}
