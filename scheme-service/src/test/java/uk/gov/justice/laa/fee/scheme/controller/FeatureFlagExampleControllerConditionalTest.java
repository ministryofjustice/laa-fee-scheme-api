package uk.gov.justice.laa.fee.scheme.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;

class FeatureFlagExampleControllerConditionalTest {

    private static class TestFeatureFlagsConfig extends FeatureFlagsConfig {
        private final boolean enabled;
        private final boolean requestOverridesEnabled;

        private TestFeatureFlagsConfig(boolean enabled, boolean requestOverridesEnabled) {
            this.enabled = enabled;
            this.requestOverridesEnabled = requestOverridesEnabled;
            setIsFeatureEnabled(enabled);
            setRequestOverridesEnabled(requestOverridesEnabled);
        }

        @Override
        public Boolean getIsFeatureEnabled() {
            return enabled;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    private static WebApplicationContextRunner runner(boolean requestOverridesEnabled) {
        return new WebApplicationContextRunner()
                .withBean(FeatureFlagsConfig.class,
                        () -> new TestFeatureFlagsConfig(false, requestOverridesEnabled))
                .withUserConfiguration(FeatureFlagExampleController.class);
    }

    @Test
    void shouldRegisterControllerWhenRequestOverridesAreDisabled() {
        runner(false).run(context ->
                assertThat(context).hasSingleBean(FeatureFlagExampleController.class));
    }

    @Test
    void shouldRegisterControllerWhenRequestOverridesAreEnabled() {
        runner(true).run(context ->
                assertThat(context).hasSingleBean(FeatureFlagExampleController.class));
    }
}