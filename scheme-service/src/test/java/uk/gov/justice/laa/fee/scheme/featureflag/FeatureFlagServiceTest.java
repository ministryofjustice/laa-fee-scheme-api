package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class FeatureFlagServiceTest {

  private FeatureFlagProperties featureFlagProperties;

  private FeatureFlagService featureFlagService;

  @BeforeEach
  void setUp() {
    featureFlagProperties = new FeatureFlagProperties();
    featureFlagService = new FeatureFlagService(featureFlagProperties);
  }

  @Test
  void isEnabled_whenFlagConfiguredAsTrue_shouldReturnTrue() {
    featureFlagProperties.setFlags(Map.of(FeatureFlags.INQUEST, true));

    boolean result = featureFlagService.isEnabled(FeatureFlags.INQUEST);

    assertThat(result).isTrue();
  }

  @Test
  void isEnabled_whenFlagConfiguredAsFalse_shouldReturnFalse() {
    featureFlagProperties.setFlags(Map.of(FeatureFlags.INQUEST, false));

    boolean result = featureFlagService.isEnabled(FeatureFlags.INQUEST);

    assertThat(result).isFalse();
  }

  @Test
  void isEnabled_whenFlagMissing_shouldReturnFalse() {
    boolean result = featureFlagService.isEnabled(FeatureFlags.INQUEST);

    assertThat(result).isFalse();
  }

  @Test
  void isEnabled_whenFeatureNameUnknown_shouldReturnFalse() {
    featureFlagProperties.setFlags(Map.of(FeatureFlags.INQUEST, true));

    boolean result = featureFlagService.isEnabled("unknown-feature");

    assertThat(result).isFalse();
  }

  @Test
  void isEnabled_whenFlagValueIsNull_shouldReturnFalse() {
    Map<String, Boolean> flags = new HashMap<>();
    flags.put(FeatureFlags.INQUEST, null);
    featureFlagProperties.setFlags(flags);

    boolean result = featureFlagService.isEnabled(FeatureFlags.INQUEST);

    assertThat(result).isFalse();
  }

  @NullSource
  @ValueSource(strings = {"", "  "})
  @ParameterizedTest
  void isEnabled_whenFeatureNameIsNullOrBlank_shouldReturnFalse(String featureName) {
    featureFlagProperties.setFlags(Map.of(FeatureFlags.INQUEST, true));

    boolean result = featureFlagService.isEnabled(featureName);

    assertThat(result).isFalse();
  }

  @Test
  void getFlags_shouldDefaultToEmptyMap() {
    assertThat(new FeatureFlagProperties().getFlags()).isEmpty();
  }
}
