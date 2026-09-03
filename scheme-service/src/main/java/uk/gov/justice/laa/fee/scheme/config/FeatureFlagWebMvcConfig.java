package uk.gov.justice.laa.fee.scheme.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagInterceptor;

/**
 * Adds feature flag endpoint gating to Spring MVC.
 */
@Configuration
@RequiredArgsConstructor
public class FeatureFlagWebMvcConfig implements WebMvcConfigurer {

  private final FeatureFlagInterceptor featureFlagInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(featureFlagInterceptor).order(1);
  }
}