package uk.gov.justice.laa.fee.scheme.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagInterceptor;
import uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagRequestOverrideInterceptor;

/** Registers request overrides before endpoint feature checks. */
@Configuration
@RequiredArgsConstructor
public class FeatureFlagWebMvcConfig implements WebMvcConfigurer {

  private final FeatureFlagRequestOverrideInterceptor requestOverrideInterceptor;
  private final FeatureFlagInterceptor featureFlagInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestOverrideInterceptor).order(0);
    registry.addInterceptor(featureFlagInterceptor).order(1);
  }
}
