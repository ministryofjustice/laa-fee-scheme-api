package uk.gov.justice.laa.fee.scheme.annotations;

import uk.gov.justice.laa.fee.scheme.config.features.Feature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFeatureFlag {
    Feature[] value();
}
