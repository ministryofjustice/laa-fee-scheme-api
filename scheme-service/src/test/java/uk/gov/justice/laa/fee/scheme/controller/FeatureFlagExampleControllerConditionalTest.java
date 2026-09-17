package uk.gov.justice.laa.fee.scheme.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;

final class FeatureFlagExampleControllerConditionalTest {

  private static FeatureFlagsConfig config(boolean requestOverridesEnabled) {
    FeatureFlagsConfig config = new FeatureFlagsConfig();
    config.setIsFeatureEnabled(false);
    config.setRequestOverridesEnabled(requestOverridesEnabled);
    return config;
  }

  @Test
  void shouldStartWithOverridesDisabled() {
    new WebApplicationContextRunner()
            .withBean(FeatureFlagsConfig.class, () -> config(false))
            .withUserConfiguration(FeatureFlagExampleController.class)
            .run(context -> assertThat(context).hasNotFailed());
  }

  @Test
  void shouldStartWithOverridesEnabled() {
    new WebApplicationContextRunner()
            .withBean(FeatureFlagsConfig.class, () -> config(true))
            .withUserConfiguration(FeatureFlagExampleController.class)
            .run(context -> assertThat(context).hasNotFailed());
  }
}
