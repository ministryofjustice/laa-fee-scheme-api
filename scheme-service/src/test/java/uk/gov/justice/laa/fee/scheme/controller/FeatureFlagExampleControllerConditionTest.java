package uk.gov.justice.laa.fee.scheme.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import uk.gov.justice.laa.fee.scheme.featureflag.FeatureFlagProperties;
import uk.gov.justice.laa.fee.scheme.featureflag.FeatureFlagService;

class FeatureFlagExampleControllerConditionTest {

  private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
      .withBean(
          FeatureFlagService.class,
          () -> new FeatureFlagService(new FeatureFlagProperties(Map.of(), false)))
      .withUserConfiguration(FeatureFlagExampleController.class);

  @Test
  void shouldNotRegisterControllerWhenRequestOverridesAreDisabled() {
    contextRunner
        .withPropertyValues("feature-flags.request-overrides-enabled=false")
        .run(context ->
            assertThat(context).doesNotHaveBean(FeatureFlagExampleController.class));
  }

  @Test
  void shouldRegisterControllerWhenRequestOverridesAreEnabled() {
    contextRunner
        .withPropertyValues("feature-flags.request-overrides-enabled=true")
        .run(context ->
            assertThat(context).hasSingleBean(FeatureFlagExampleController.class));
  }
}
