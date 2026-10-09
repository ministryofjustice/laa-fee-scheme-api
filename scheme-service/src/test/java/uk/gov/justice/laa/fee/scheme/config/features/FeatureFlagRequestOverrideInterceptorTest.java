package uk.gov.justice.laa.fee.scheme.config.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.exception.InvalidFeatureFlagRequestOverrideException;

class FeatureFlagRequestOverrideInterceptorTest {

  private final FeatureFlagsConfig flags = new FeatureFlagsConfig();
  private final FeatureFlagRequestOverrideInterceptor interceptor =
      new FeatureFlagRequestOverrideInterceptor(flags);

  @AfterEach
  void clearRequest() {
    RequestContextHolder.resetRequestAttributes();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void overrideWinsWithoutChangingConfiguredValue(boolean enabled) {
    flags.setIsExampleFeatureEnabled(!enabled);
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("EXAMPLE_FEATURE:" + enabled);
    assertThat(interceptor.preHandle(request, new MockHttpServletResponse(), new Object())).isTrue();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.isEnabled(Feature.EXAMPLE_FEATURE)).isEqualTo(enabled);
    assertThat(flags.getIsExampleFeatureEnabled()).isEqualTo(enabled);
    RequestContextHolder.resetRequestAttributes();
    assertThat(flags.getIsExampleFeatureEnabled()).isEqualTo(!enabled);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "", "EXAMPLE_FEATURE", "EXAMPLE_FEATURE:", ":true", "EXAMPLE_FEATURE:maybe", "EXAMPLE_FEATURE:true:extra",
      "UNKNOWN:true", "feature:true", "EXAMPLE_FEATURE: true", " EXAMPLE_FEATURE:true"
  })
  void rejectsMalformedOrUnknownOverrides(String parameter) {
    flags.setRequestOverridesEnabled(true);
    var request = requestWith(parameter);
    assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class);
    assertThat(request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE)).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"EXAMPLE_FEATURE:true", "EXAMPLE_FEATURE:false"})
  void rejectsDuplicatesEvenWhenFirstValueIsFalse(String second) {
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("EXAMPLE_FEATURE:false", second);
    assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class)
        .hasMessageContaining("Duplicate");
    assertThat(request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE)).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"TRUE", "True", "FALSE", "False"})
  void acceptsCaseInsensitiveBooleans(String value) {
    flags.setIsExampleFeatureEnabled(false);
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("EXAMPLE_FEATURE:" + value);
    interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.getIsExampleFeatureEnabled()).isEqualTo(Boolean.parseBoolean(value));
  }

  @Test
  void concurrentRequestsRemainIsolated() throws Exception {
    flags.setIsExampleFeatureEnabled(false);
    flags.setRequestOverridesEnabled(true);
    CyclicBarrier barrier = new CyclicBarrier(2);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var enabled = executor.submit(() -> evaluateConcurrentRequest(true, barrier));
      var disabled = executor.submit(() -> evaluateConcurrentRequest(false, barrier));
      assertThat(enabled.get(10, TimeUnit.SECONDS)).isTrue();
      assertThat(disabled.get(10, TimeUnit.SECONDS)).isFalse();
    }
    assertThat(flags.getIsExampleFeatureEnabled()).isFalse();
  }

  @Test
  void storageIsImmutableAndAcceptsEmptyMaps() {
    Map<Feature, Boolean> values = new HashMap<>();
    values.put(Feature.EXAMPLE_FEATURE, true);
    var overrides = new FeatureFlagRequestOverrides(values);
    values.put(Feature.EXAMPLE_FEATURE, false);
    assertThat(overrides.values()).containsEntry(Feature.EXAMPLE_FEATURE, true);
    assertThatThrownBy(() -> overrides.values().put(Feature.EXAMPLE_FEATURE, false))
        .isInstanceOf(UnsupportedOperationException.class);
    assertThat(new FeatureFlagRequestOverrides(Map.of()).values()).isEmpty();
  }

  @Test
  void absentOverrideAndDisabledCapabilityUseConfiguredValue() {
    flags.setIsExampleFeatureEnabled(true);
    flags.setRequestOverridesEnabled(true);
    var request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.getIsExampleFeatureEnabled()).isTrue();
    request.setAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(Map.of()));
    assertThat(flags.getIsExampleFeatureEnabled()).isTrue();
    request.setAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(Map.of(Feature.EXAMPLE_FEATURE, false)));
    flags.setRequestOverridesEnabled(false);
    assertThat(flags.getIsExampleFeatureEnabled()).isTrue();
  }

  private boolean evaluateConcurrentRequest(boolean enabled, CyclicBarrier barrier) throws Exception {
    var request = requestWith("EXAMPLE_FEATURE:" + enabled);
    interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    try {
      barrier.await(5, TimeUnit.SECONDS);
      return flags.getIsExampleFeatureEnabled();
    } finally {
      RequestContextHolder.resetRequestAttributes();
    }
  }

  private MockHttpServletRequest requestWith(String... parameters) {
    var request = new MockHttpServletRequest();
    request.addParameter("featureFlag", parameters);
    return request;
  }
}
