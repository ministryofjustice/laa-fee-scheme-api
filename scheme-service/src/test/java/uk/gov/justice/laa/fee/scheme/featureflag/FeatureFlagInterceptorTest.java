package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class FeatureFlagInterceptorTest {

  @Mock private FeatureFlagService featureFlagService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private FeatureFlagInterceptor interceptor;

  @BeforeEach
  void setUp() {
    interceptor = new FeatureFlagInterceptor(featureFlagService);
  }

  @Test
  void shouldIgnoreHandlersThatAreNotControllerMethods() {
    assertThat(interceptor.preHandle(request, response, new Object())).isTrue();

    verify(featureFlagService, never()).isEnabled(FeatureFlag.EXAMPLE_FEATURE);
  }

  @Test
  void shouldAllowControllerMethodWithoutRequiredFeature() throws NoSuchMethodException {
    HandlerMethod handlerMethod = handlerMethod(new UnflaggedController(), "get");

    assertThat(interceptor.preHandle(request, response, handlerMethod)).isTrue();

    verify(featureFlagService, never()).isEnabled(FeatureFlag.EXAMPLE_FEATURE);
  }

  @Test
  void shouldAllowControllerMethodWhenRequiredFeatureIsEnabled() throws NoSuchMethodException {
    when(featureFlagService.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).thenReturn(true);
    HandlerMethod handlerMethod = handlerMethod(new MethodFlaggedController(), "get");

    assertThat(interceptor.preHandle(request, response, handlerMethod)).isTrue();
  }

  @Test
  void shouldRejectControllerMethodWhenRequiredFeatureIsDisabled() throws NoSuchMethodException {
    HandlerMethod handlerMethod = handlerMethod(new MethodFlaggedController(), "get");

    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(FeatureDisabledException.class)
        .hasMessage("Feature is not available: example-feature");
  }

  @Test
  void shouldRejectControllerWhenRequiredFeatureIsDisabled() throws NoSuchMethodException {
    HandlerMethod handlerMethod = handlerMethod(new ClassFlaggedController(), "get");

    assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
        .isInstanceOf(FeatureDisabledException.class);
  }

  private HandlerMethod handlerMethod(Object controller, String methodName)
      throws NoSuchMethodException {
    return new HandlerMethod(controller, controller.getClass().getMethod(methodName));
  }

  private static final class UnflaggedController {

    public void get() {
    }
  }

  private static final class MethodFlaggedController {

    @RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
    public void get() {
    }
  }

  @RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
  private static final class ClassFlaggedController {

    public void get() {
    }
  }
}
