package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class FeatureFlagRequestOverrideInterceptorTest {

  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @Test
  void shouldIgnoreRequestWithoutOverrides() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    assertThat(interceptor(true).preHandle(request, response, new Object())).isTrue();
    assertThat(request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE)).isNull();
  }

  @Test
  void shouldRejectOverrideWhenRequestOverridesAreDisabled() {
    MockHttpServletRequest request = requestWithOverrides("example-feature:true");

    assertThatThrownBy(() -> interceptor(false).preHandle(request, response, new Object()))
        .isInstanceOf(FeatureFlagRequestOverrideNotAllowedException.class)
        .hasMessage("Feature flag request overrides are not enabled");
  }

  @Test
  void shouldStoreValidOverrideOnRequest() {
    MockHttpServletRequest request = requestWithOverrides("example-feature:true");

    assertThat(interceptor(true).preHandle(request, response, new Object())).isTrue();

    FeatureFlagRequestOverrides overrides = (FeatureFlagRequestOverrides)
        request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE);
    assertThat(overrides.get(FeatureFlag.EXAMPLE_FEATURE)).contains(true);
  }

  @Test
  void shouldAcceptCaseInsensitiveBoolean() {
    MockHttpServletRequest request = requestWithOverrides("example-feature:FALSE");

    interceptor(true).preHandle(request, response, new Object());

    FeatureFlagRequestOverrides overrides = (FeatureFlagRequestOverrides)
        request.getAttribute(FeatureFlagRequestOverrides.REQUEST_ATTRIBUTE);
    assertThat(overrides.get(FeatureFlag.EXAMPLE_FEATURE)).contains(false);
  }

  @Test
  void shouldRejectUnknownFeatureFlag() {
    MockHttpServletRequest request = requestWithOverrides("unknown-feature:true");

    assertThatThrownBy(() -> interceptor(true).preHandle(request, response, new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class)
        .hasMessage("Unknown feature flag request override: unknown-feature");
  }

  @Test
  void shouldRejectDuplicateFeatureFlag() {
    MockHttpServletRequest request = requestWithOverrides(
        "example-feature:true",
        "example-feature:false");

    assertThatThrownBy(() -> interceptor(true).preHandle(request, response, new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class)
        .hasMessage("Duplicate feature flag request override: example-feature");
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "",
      "example-feature",
      "example-feature:",
      ":true",
      "example-feature:yes",
      "example-feature:true:extra"
  })
  void shouldRejectInvalidOverrideFormat(String configuredOverride) {
    MockHttpServletRequest request = requestWithOverrides(configuredOverride);

    assertThatThrownBy(() -> interceptor(true).preHandle(request, response, new Object()))
        .isInstanceOf(InvalidFeatureFlagRequestOverrideException.class)
        .hasMessageContaining("Expected <flag-key>:<true|false>");
  }

  private FeatureFlagRequestOverrideInterceptor interceptor(boolean requestOverridesEnabled) {
    return new FeatureFlagRequestOverrideInterceptor(
        new FeatureFlagProperties(Map.of(), requestOverridesEnabled));
  }

  private MockHttpServletRequest requestWithOverrides(String... overrides) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER, overrides);
    return request;
  }
}
