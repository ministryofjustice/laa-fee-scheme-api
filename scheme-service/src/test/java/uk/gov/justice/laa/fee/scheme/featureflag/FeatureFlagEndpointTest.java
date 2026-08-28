package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.mockito.Mockito.when;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(FeatureFlagEndpointTest.FlaggedController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({FeatureFlagConfiguration.class, FeatureFlagEndpointTest.FlaggedController.class})
class FeatureFlagEndpointTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private FeatureFlagService featureFlagService;

  @Test
  void shouldReturnNotFoundWhenEndpointFeatureIsDisabled() throws Exception {
    mockMvc
        .perform(get("/flagged"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Feature is not available: example-feature"));
  }

  @Test
  void shouldCallEndpointWhenFeatureIsEnabled() throws Exception {
    when(featureFlagService.isEnabled(FeatureFlag.EXAMPLE_FEATURE)).thenReturn(true);

    mockMvc
        .perform(get("/flagged"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string("enabled"));
  }

  /**
   * Test-only controller used to prove feature-gated Spring MVC wiring.
   */
  @RestController
  public static final class FlaggedController {

    @GetMapping("/flagged")
    @RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
    public String get() {
      return "enabled";
    }
  }
}
