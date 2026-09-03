package uk.gov.justice.laa.fee.scheme.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;
import uk.gov.justice.laa.fee.scheme.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.config.features.FeatureFlagInterceptor;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;

class FeatureFlagsConfigTest {

  @Test
  void checkEnabled_whenFeatureEnabled_doesNotThrow() {
    FeatureFlagsConfig featureFlagsConfig = new FeatureFlagsConfig();
    featureFlagsConfig.setIsFeatureEnabled(true);

    assertThatCode(() -> featureFlagsConfig.checkEnabled(Feature.FEATURE))
        .doesNotThrowAnyException();
  }

  @Test
  void checkEnabled_whenFeatureDisabled_throwsFeatureNotEnabledException() {
    FeatureFlagsConfig featureFlagsConfig = new FeatureFlagsConfig();
    featureFlagsConfig.setIsFeatureEnabled(false);

    assertThatThrownBy(() -> featureFlagsConfig.checkEnabled(Feature.FEATURE))
        .isInstanceOf(FeatureNotEnabledException.class)
        .hasMessage("Feature is not available: FEATURE");
  }

  @Test
  void preHandle_whenMethodHasFeatureAnnotationAndFeatureEnabled_returnsTrue()
      throws NoSuchMethodException {
    FeatureFlagsConfig featureFlagsConfig = new FeatureFlagsConfig();
    featureFlagsConfig.setIsFeatureEnabled(true);
    FeatureFlagInterceptor interceptor = new FeatureFlagInterceptor(featureFlagsConfig);

    HandlerMethod handlerMethod =
        new HandlerMethod(
            new FeatureFlagController(),
            FeatureFlagController.class.getDeclaredMethod("enabledMethod"));

    assertThat(
            interceptor.preHandle(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), handlerMethod))
        .isTrue();
  }

  @Test
  void preHandle_whenMethodHasFeatureAnnotationAndFeatureDisabled_throwsFeatureNotEnabledException()
      throws NoSuchMethodException {
    FeatureFlagsConfig featureFlagsConfig = new FeatureFlagsConfig();
    featureFlagsConfig.setIsFeatureEnabled(false);
    FeatureFlagInterceptor interceptor = new FeatureFlagInterceptor(featureFlagsConfig);

    HandlerMethod handlerMethod =
        new HandlerMethod(
            new FeatureFlagController(),
            FeatureFlagController.class.getDeclaredMethod("enabledMethod"));

    assertThatThrownBy(
            () ->
                interceptor.preHandle(
                    mock(HttpServletRequest.class), mock(HttpServletResponse.class), handlerMethod))
        .isInstanceOf(FeatureNotEnabledException.class)
        .hasMessage("Feature is not available: FEATURE");
  }

  @Test
  void preHandle_whenHandlerIsNotHandlerMethod_returnsTrue() {
    FeatureFlagsConfig featureFlagsConfig = new FeatureFlagsConfig();
    featureFlagsConfig.setIsFeatureEnabled(false);
    FeatureFlagInterceptor interceptor = new FeatureFlagInterceptor(featureFlagsConfig);

    assertThat(
            interceptor.preHandle(
                mock(HttpServletRequest.class), mock(HttpServletResponse.class), new Object()))
        .isTrue();
  }

  private static final class FeatureFlagController {

    @RequiresFeatureFlag(Feature.FEATURE)
    public void enabledMethod() {
      // no-op
    }
  }
}
