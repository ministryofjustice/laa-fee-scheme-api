package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(FeatureFlagEndpointTest.FlaggedController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({FeatureFlagConfiguration.class, FeatureFlagEndpointTest.FlaggedController.class})
@TestPropertySource(properties = "feature-flags.request-overrides-enabled=true")
class FeatureFlagEndpointTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnNotFoundWhenEndpointFeatureIsDisabled() throws Exception {
    mockMvc
        .perform(get("/flagged"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Feature is not available: example-feature"));
  }

  @Test
  void shouldCallEndpointWhenFeatureIsEnabledForRequest() throws Exception {
    mockMvc
        .perform(get("/flagged")
            .param(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER,
                "example-feature:true"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("enabled"));
  }

  @Test
  void shouldApplyOverridesOnlyToTheCurrentRequest() throws Exception {
    mockMvc
        .perform(get("/inline")
            .param(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER,
                "example-feature:true"))
        .andExpect(status().isOk())
        .andExpect(content().string("enabled"));

    mockMvc
        .perform(get("/inline"))
        .andExpect(status().isOk())
        .andExpect(content().string("disabled"));
  }

  @Test
  void shouldDisableInlineFeatureForRequest() throws Exception {
    mockMvc
        .perform(get("/inline")
            .param(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER,
                "example-feature:false"))
        .andExpect(status().isOk())
        .andExpect(content().string("disabled"));
  }

  @Test
  void shouldRejectInvalidRequestOverride() throws Exception {
    mockMvc
        .perform(get("/inline")
            .param(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER,
                "unknown-feature:true"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("Unknown feature flag request override: unknown-feature"));
  }

  /**
   * Test-only controller used to prove feature-gated Spring MVC wiring.
   */
  @RestController
  public static final class FlaggedController {

    private final FeatureFlagService featureFlagService;

    public FlaggedController(FeatureFlagService featureFlagService) {
      this.featureFlagService = featureFlagService;
    }

    @GetMapping("/flagged")
    @RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
    public String get() {
      return "enabled";
    }

    @GetMapping("/inline")
    public String getInline() {
      return featureFlagService.isEnabled(FeatureFlag.EXAMPLE_FEATURE)
          ? "enabled"
          : "disabled";
    }
  }
}
