package uk.gov.justice.laa.fee.scheme.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.config.features.Feature;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest(properties = {
    "feature-flags.is-feature-enabled=false",
    "feature-flags.request-overrides-enabled=true"
})
@AutoConfigureMockMvc
@Import(FeatureFlagIntegrationTest.TestController.class)
class FeatureFlagIntegrationTest extends PostgresContainerTestBase {

  @Autowired private MockMvc mockMvc;

  @Test
  void authenticatedRequestCanOverrideBothInlineAndEndpointChecks() throws Exception {
    mockMvc.perform(get("/test-features/gated").header(HttpHeaders.AUTHORIZATION, "int-test-token"))
        .andExpect(status().isNotFound());
    mockMvc.perform(get("/test-features/gated").header(HttpHeaders.AUTHORIZATION, "int-test-token")
            .param("featureFlag", "FEATURE:true"))
        .andExpect(status().isOk()).andExpect(content().string("true"));
    mockMvc.perform(get("/test-features/inline").header(HttpHeaders.AUTHORIZATION, "int-test-token")
            .param("featureFlag", "FEATURE:false"))
        .andExpect(status().isOk()).andExpect(content().string("false"));
    mockMvc.perform(get("/test-features/gated").header(HttpHeaders.AUTHORIZATION, "int-test-token"))
        .andExpect(status().isNotFound());
  }

  @Test
  void overrideCannotBypassAuthentication() throws Exception {
    mockMvc.perform(get("/test-features/gated").param("featureFlag", "FEATURE:true"))
        .andExpect(status().isUnauthorized());
    mockMvc.perform(get("/test-features/gated").header(HttpHeaders.AUTHORIZATION, "wrong-token")
            .param("featureFlag", "FEATURE:true"))
        .andExpect(status().isUnauthorized());
  }

  @TestComponent
  @RestController
  static class TestController {
    private final FeatureFlagsConfig flags;

    TestController(FeatureFlagsConfig flags) {
      this.flags = flags;
    }

    @GetMapping("/test-features/gated")
    @RequiresFeatureFlag(Feature.FEATURE)
    public boolean gated() {
      return flags.getIsFeatureEnabled();
    }

    @GetMapping("/test-features/inline")
    public boolean inline() {
      return flags.isEnabled(Feature.FEATURE);
    }
  }
}
