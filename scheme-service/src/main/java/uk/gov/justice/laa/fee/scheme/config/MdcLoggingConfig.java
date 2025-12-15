package uk.gov.justice.laa.fee.scheme.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.justice.laa.fee.scheme.logback.MdcLoggingInterceptor;

/**
 * MDC logging config for interceptor.
 */
@Configuration
@RequiredArgsConstructor
public class MdcLoggingConfig implements WebMvcConfigurer {

  private final MdcLoggingInterceptor mdcLoggingInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(mdcLoggingInterceptor);
  }
}
