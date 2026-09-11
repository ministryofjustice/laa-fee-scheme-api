package uk.gov.justice.laa.fee.scheme.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotEnabledException;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotImplementedRuntimeException;

class FeatureFlagsConfigTest {

  private final ApplicationContextRunner runner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
          ConfigurationPropertiesAutoConfiguration.class, ValidationAutoConfiguration.class))
      .withUserConfiguration(FeatureFlagsConfig.class);

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void bindsAndEvaluatesConfiguredValue(boolean enabled) {
    runner.withPropertyValues("feature-flags.is-feature-enabled=" + enabled)
        .run(context -> {
          assertThat(context).hasNotFailed();
          FeatureFlagsConfig flags = context.getBean(FeatureFlagsConfig.class);
          assertThat(flags.isEnabled(Feature.FEATURE)).isEqualTo(enabled);
          assertThat(flags.getIsFeatureEnabled()).isEqualTo(enabled);
          assertThat(flags.isRequestOverridesEnabled()).isFalse();
          if (enabled) {
            assertThatCode(() -> flags.checkEnabled(Feature.FEATURE)).doesNotThrowAnyException();
          } else {
            assertThatThrownBy(() -> flags.checkEnabled(Feature.FEATURE))
                .isInstanceOf(FeatureNotEnabledException.class)
                .hasMessage("Feature is not available: FEATURE");
          }
        });
  }

  @Test
  void missingFlagFailsStartup() {
    runner.run(context -> {
      assertThat(context).hasFailed();
      assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(BindValidationException.class);
    });
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "feature-flags.is-feature-enabled=maybe",
      "feature-flags.is-feature-enabled=",
      "feature-flags.request-overrides-enabled=maybe",
      "feature-flags.unknown-feature=true"
  })
  void invalidConfigurationFailsStartup(String invalidProperty) {
    runner.withPropertyValues("feature-flags.is-feature-enabled=true", invalidProperty)
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void applicationYamlBindsEnvironmentVariables() throws IOException {
    var source = new YamlPropertySourceLoader()
        .load("application", new ClassPathResource("application.yml")).getFirst();
    runner.withInitializer(context -> context.getEnvironment().getPropertySources().addLast(source))
        .withPropertyValues("IS_FEATURE_ENABLED=false", "FEATURE_FLAG_REQUEST_OVERRIDES_ENABLED=true")
        .run(context -> {
          assertThat(context).hasNotFailed();
          FeatureFlagsConfig flags = context.getBean(FeatureFlagsConfig.class);
          assertThat(flags.getIsFeatureEnabled()).isFalse();
          assertThat(flags.isRequestOverridesEnabled()).isTrue();
        });
  }

  @Test
  void missingEnvironmentVariableUsesApplicationYamlDefault() throws IOException {
    var source = new YamlPropertySourceLoader()
        .load("application", new ClassPathResource("application.yml")).getFirst();
    runner.withInitializer(context -> context.getEnvironment().getPropertySources().addLast(source))
        .run(context -> {
          assertThat(context).hasNotFailed();
          FeatureFlagsConfig flags = context.getBean(FeatureFlagsConfig.class);
          assertThat(flags.getIsFeatureEnabled()).isTrue();
          assertThat(flags.isRequestOverridesEnabled()).isFalse();
        });
  }

  @Test
  void unconfiguredManualInstanceDoesNotSilentlyDisableFeature() {
    assertThatThrownBy(() -> new FeatureFlagsConfig().isEnabled(Feature.FEATURE))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Missing feature flag configuration");
  }

  @Test
  void invalidJavaFeatureIsReportedExplicitly() {
    assertThatThrownBy(() -> new FeatureFlagsConfig().isEnabled(null))
        .isInstanceOf(FeatureNotImplementedRuntimeException.class);
  }
}
