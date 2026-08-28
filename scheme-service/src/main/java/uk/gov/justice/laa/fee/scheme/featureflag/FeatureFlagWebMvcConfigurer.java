package uk.gov.justice.laa.fee.scheme.featureflag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Adds feature flag endpoint gating to Spring MVC.
 */
@RequiredArgsConstructor
public class FeatureFlagWebMvcConfigurer implements WebMvcConfigurer {

  private final FeatureFlagInterceptor featureFlagInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(featureFlagInterceptor).order(1);
  }
}
