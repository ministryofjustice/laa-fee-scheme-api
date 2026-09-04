package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class FeatureFlagServiceTest {

  @AfterEach
  void clearRequest() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void shouldUseConfiguredValueWithoutRequestOverride() {
    FeatureFlagService service = service(true, true);

    assertThat(service.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isTrue();
  }

  @Test
  void shouldUseRequestOverrideBeforeConfiguredValue() {
    bindRequestOverride(false);
    FeatureFlagService service = service(true, true);

    assertThat(service.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isFalse();
  }

  @Test
  void shouldIgnoreRequestAttributeWhenOverridesAreDisabled() {
    bindRequestOverride(true);
    FeatureFlagService service = service(false, false);

    assertThat(service.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isFalse();
  }

  @Test
  void shouldNotLeakOverrideIntoAnotherRequest() {
    FeatureFlagService service = service(false, true);
    bindRequestOverride(true);
    assertThat(service.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isTrue();

    RequestContextHolder.setRequestAttributes(
        new ServletRequestAttributes(new MockHttpServletRequest()));

    assertThat(service.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isFalse();
  }

  @Test
  void shouldKeepConcurrentRequestOverridesIsolated() throws Exception {
    FeatureFlagService service = service(false, true);
    CyclicBarrier requestsReady = new CyclicBarrier(2);
    ExecutorService executor = Executors.newFixedThreadPool(2);

    try {
      Future<Boolean> enabledRequest =
          executor.submit(() -> evaluateRequest(service, true, requestsReady));
      Future<Boolean> disabledRequest =
          executor.submit(() -> evaluateRequest(service, false, requestsReady));

      assertThat(enabledRequest.get()).isTrue();
      assertThat(disabledRequest.get()).isFalse();
    } finally {
      executor.shutdownNow();
    }
  }

  private FeatureFlagService service(boolean configuredValue, boolean requestOverridesEnabled) {
    return new FeatureFlagService(new FeatureFlagProperties(
        Map.of(FeatureFlag.EXAMPLE_FEATURE.key(), configuredValue),
        requestOverridesEnabled));
  }

  private void bindRequestOverride(boolean enabled) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute(
        FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(Map.of(FeatureFlag.EXAMPLE_FEATURE, enabled)));
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  private boolean evaluateRequest(
      FeatureFlagService service,
      boolean enabled,
      CyclicBarrier requestsReady) throws Exception {
    try {
      bindRequestOverride(enabled);
      requestsReady.await();
      return service.isEnabled(FeatureFlag.EXAMPLE_FEATURE);
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }
}
