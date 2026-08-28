package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@ExtendWith(MockitoExtension.class)
class FeatureFlagWebMvcConfigurerTest {

  @Mock private FeatureFlagInterceptor featureFlagInterceptor;
  @Mock private InterceptorRegistry registry;
  @Mock private InterceptorRegistration registration;

  @Test
  void shouldRegisterFeatureFlagInterceptorAfterLoggingInterceptor() {
    when(registry.addInterceptor(featureFlagInterceptor)).thenReturn(registration);

    new FeatureFlagWebMvcConfigurer(featureFlagInterceptor).addInterceptors(registry);

    verify(registration).order(1);
  }
}
