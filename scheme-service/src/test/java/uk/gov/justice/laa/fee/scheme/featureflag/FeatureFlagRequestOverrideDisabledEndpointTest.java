package uk.gov.justice.laa.fee.scheme.featureflag;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(FeatureFlagRequestOverrideDisabledEndpointTest.TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
    FeatureFlagConfiguration.class,
    FeatureFlagRequestOverrideDisabledEndpointTest.TestController.class
})
class FeatureFlagRequestOverrideDisabledEndpointTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldRejectRequestOverrideWhenOverridesAreDisabled() throws Exception {
    mockMvc
        .perform(get("/unflagged")
            .param(FeatureFlagRequestOverrideInterceptor.QUERY_PARAMETER,
                "example-feature:true"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message")
            .value("Feature flag request overrides are not enabled"));
  }

  @RestController
  static final class TestController {

    @GetMapping("/unflagged")
    String get() {
      return "available";
    }
  }
}
