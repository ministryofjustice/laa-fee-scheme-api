package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class FeatureFlagConfigurationTest {

  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withUserConfiguration(FeatureFlagConfiguration.class);

  @Test
  void shouldBindEnabledFlagFromConfiguration() {
    contextRunner
        .withPropertyValues("feature-flags.flags.example-feature=true")
        .run(context -> {
          assertThat(context).hasSingleBean(FeatureFlagService.class);
          assertThat(context.getBean(FeatureFlagService.class)
              .isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isTrue();
        });
  }

  @Test
  void shouldDefaultMissingFlagToDisabled() {
    contextRunner.run(context ->
        assertThat(context.getBean(FeatureFlagService.class)
            .isEnabled(FeatureFlag.EXAMPLE_FEATURE)).isFalse());
  }

  @Test
  void shouldRejectUnknownFlagConfiguration() {
    contextRunner
        .withPropertyValues("feature-flags.flags.unknown-feature=true")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasRootCauseMessage("Unknown feature flag configuration: unknown-feature");
        });
  }
}
