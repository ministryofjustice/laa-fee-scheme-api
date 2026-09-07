package uk.gov.justice.laa.fee.scheme.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.fee.scheme.featureflag.FeatureFlagConfiguration;

@WebMvcTest(FeatureFlagExampleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(FeatureFlagConfiguration.class)
@TestPropertySource(properties = "feature-flags.request-overrides-enabled=true")
class FeatureFlagExampleControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnConfiguredStateWithoutOverride() throws Exception {
    mockMvc
        .perform(get("/feature-flags/example-feature"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.featureFlag").value("example-feature"))
        .andExpect(jsonPath("$.enabled").value(false));
  }

  @Test
  void shouldReturnRequestOverrideState() throws Exception {
    mockMvc
        .perform(get("/feature-flags/example-feature")
            .param("featureFlag", "example-feature:true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.featureFlag").value("example-feature"))
        .andExpect(jsonPath("$.enabled").value(true));
  }

  @Test
  void shouldHideGatedEndpointWhenFeatureIsDisabled() throws Exception {
    mockMvc
        .perform(get("/feature-flags/example-feature/gated"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message")
            .value("Feature is not available: example-feature"));
  }

  @Test
  void shouldExposeGatedEndpointForEnabledRequest() throws Exception {
    mockMvc
        .perform(get("/feature-flags/example-feature/gated")
            .param("featureFlag", "example-feature:true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.featureFlag").value("example-feature"))
        .andExpect(jsonPath("$.enabled").value(true));
  }
}
