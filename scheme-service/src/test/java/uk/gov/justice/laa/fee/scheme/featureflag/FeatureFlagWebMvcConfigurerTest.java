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

  @Mock private FeatureFlagRequestOverrideInterceptor featureFlagRequestOverrideInterceptor;
  @Mock private FeatureFlagInterceptor featureFlagInterceptor;
  @Mock private InterceptorRegistry registry;
  @Mock private InterceptorRegistration requestOverrideRegistration;
  @Mock private InterceptorRegistration featureFlagRegistration;

  @Test
  void shouldRegisterFeatureFlagInterceptorAfterLoggingInterceptor() {
    when(registry.addInterceptor(featureFlagRequestOverrideInterceptor))
        .thenReturn(requestOverrideRegistration);
    when(registry.addInterceptor(featureFlagInterceptor)).thenReturn(featureFlagRegistration);

    new FeatureFlagWebMvcConfigurer(
        featureFlagRequestOverrideInterceptor,
        featureFlagInterceptor).addInterceptors(registry);

    verify(requestOverrideRegistration).order(1);
    verify(featureFlagRegistration).order(2);
  }
}
