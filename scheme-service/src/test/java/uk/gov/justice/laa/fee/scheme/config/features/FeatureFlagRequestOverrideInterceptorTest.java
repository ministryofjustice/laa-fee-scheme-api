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
    flags.setIsFeatureEnabled(!enabled);
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("FEATURE:" + enabled);
    assertThat(interceptor.preHandle(request, new MockHttpServletResponse(), new Object())).isTrue();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.isEnabled(Feature.FEATURE)).isEqualTo(enabled);
    assertThat(flags.getIsFeatureEnabled()).isEqualTo(enabled);
    RequestContextHolder.resetRequestAttributes();
    assertThat(flags.getIsFeatureEnabled()).isEqualTo(!enabled);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "", "FEATURE", "FEATURE:", ":true", "FEATURE:maybe", "FEATURE:true:extra",
      "UNKNOWN:true", "feature:true", "FEATURE: true", " FEATURE:true"
  })
  void rejectsMalformedOrUnknownOverrides(String parameter) {
    flags.setRequestOverridesEnabled(true);
    var request = requestWith(parameter);
    assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class);
    assertThat(request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE)).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"FEATURE:true", "FEATURE:false"})
  void rejectsDuplicatesEvenWhenFirstValueIsFalse(String second) {
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("FEATURE:false", second);
    assertThatThrownBy(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class)
        .hasMessageContaining("Duplicate");
    assertThat(request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE)).isNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"TRUE", "True", "FALSE", "False"})
  void acceptsCaseInsensitiveBooleans(String value) {
    flags.setIsFeatureEnabled(false);
    flags.setRequestOverridesEnabled(true);
    var request = requestWith("FEATURE:" + value);
    interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.getIsFeatureEnabled()).isEqualTo(Boolean.parseBoolean(value));
  }

  @Test
  void concurrentRequestsRemainIsolated() throws Exception {
    flags.setIsFeatureEnabled(false);
    flags.setRequestOverridesEnabled(true);
    CyclicBarrier barrier = new CyclicBarrier(2);
    try (var executor = Executors.newFixedThreadPool(2)) {
      var enabled = executor.submit(() -> evaluateConcurrentRequest(true, barrier));
      var disabled = executor.submit(() -> evaluateConcurrentRequest(false, barrier));
      assertThat(enabled.get(10, TimeUnit.SECONDS)).isTrue();
      assertThat(disabled.get(10, TimeUnit.SECONDS)).isFalse();
    }
    assertThat(flags.getIsFeatureEnabled()).isFalse();
  }

  @Test
  void storageIsImmutableAndAcceptsEmptyMaps() {
    Map<Feature, Boolean> values = new HashMap<>();
    values.put(Feature.FEATURE, true);
    var overrides = new FeatureFlagRequestOverrides(values);
    values.put(Feature.FEATURE, false);
    assertThat(overrides.values()).containsEntry(Feature.FEATURE, true);
    assertThatThrownBy(() -> overrides.values().put(Feature.FEATURE, false))
        .isInstanceOf(UnsupportedOperationException.class);
    assertThat(new FeatureFlagRequestOverrides(Map.of()).values()).isEmpty();
  }

  @Test
  void absentOverrideAndDisabledCapabilityUseConfiguredValue() {
    flags.setIsFeatureEnabled(true);
    flags.setRequestOverridesEnabled(true);
    var request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    assertThat(flags.getIsFeatureEnabled()).isTrue();
    request.setAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(Map.of()));
    assertThat(flags.getIsFeatureEnabled()).isTrue();
    request.setAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE,
        new FeatureFlagRequestOverrides(Map.of(Feature.FEATURE, false)));
    flags.setRequestOverridesEnabled(false);
    assertThat(flags.getIsFeatureEnabled()).isTrue();
  }

  private boolean evaluateConcurrentRequest(boolean enabled, CyclicBarrier barrier) throws Exception {
    var request = requestWith("FEATURE:" + enabled);
    interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    try {
      barrier.await(5, TimeUnit.SECONDS);
      return flags.getIsFeatureEnabled();
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
