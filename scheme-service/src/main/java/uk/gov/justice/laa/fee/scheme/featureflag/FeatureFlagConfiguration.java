package uk.gov.justice.laa.fee.scheme.featureflag;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers feature flag configuration.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FeatureFlagProperties.class)
public class FeatureFlagConfiguration {

  /**
   * Creates the configuration-backed feature flag service.
   *
   * @param properties configured flag values
   * @return the feature flag service
   */
  @Bean
  public FeatureFlagService featureFlagService(FeatureFlagProperties properties) {
    return new FeatureFlagService(properties);
  }

  /**
   * Creates the request feature flag override interceptor.
   *
   * @param properties feature flag configuration
   * @return the request override interceptor
   */
  @Bean
  public FeatureFlagRequestOverrideInterceptor featureFlagRequestOverrideInterceptor(
      FeatureFlagProperties properties) {
    return new FeatureFlagRequestOverrideInterceptor(properties);
  }

  /**
   * Creates the endpoint feature flag interceptor.
   *
   * @param featureFlagService feature flag evaluation service
   * @return the interceptor
   */
  @Bean
  public FeatureFlagInterceptor featureFlagInterceptor(
      FeatureFlagService featureFlagService) {
    return new FeatureFlagInterceptor(featureFlagService);
  }

  /**
   * Registers endpoint feature flag interception with Spring MVC.
   *
   * @param featureFlagRequestOverrideInterceptor request override interceptor
   * @param featureFlagInterceptor endpoint feature flag interceptor
   * @return the Spring MVC configurer
   */
  @Bean
  public WebMvcConfigurer featureFlagWebMvcConfigurer(
      FeatureFlagRequestOverrideInterceptor featureFlagRequestOverrideInterceptor,
      FeatureFlagInterceptor featureFlagInterceptor) {
    return new FeatureFlagWebMvcConfigurer(
        featureFlagRequestOverrideInterceptor,
        featureFlagInterceptor);
  }
}
