package uk.gov.justice.laa.fee.scheme.config.features;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.annotations.RequiresFeatureFlag;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagWebMvcConfig;
import uk.gov.justice.laa.fee.scheme.config.FeatureFlagsConfig;
import uk.gov.justice.laa.fee.scheme.exception.FeatureNotImplementedRuntimeException;

@WebMvcTest(controllers = {
    FeatureFlagWebMvcTest.MethodController.class, FeatureFlagWebMvcTest.ClassController.class
}, properties = {
    "feature-flags.is-feature-enabled=false",
    "feature-flags.request-overrides-enabled=true"
})
@AutoConfigureMockMvc(addFilters = false)
@Import({FeatureFlagsConfig.class, FeatureFlagWebMvcConfig.class,
    FeatureFlagInterceptor.class, FeatureFlagRequestOverrideInterceptor.class,
    FeatureFlagWebMvcTest.MethodController.class, FeatureFlagWebMvcTest.ClassController.class})
class FeatureFlagWebMvcTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void inlineEvaluationUsesConfiguredValueAndRequestOverrides() throws Exception {
    mockMvc.perform(get("/test-flags/inline")).andExpect(content().string("false"));
    mockMvc.perform(get("/test-flags/inline").param("featureFlag", "FEATURE:true"))
        .andExpect(status().isOk()).andExpect(content().string("true"));
    mockMvc.perform(get("/test-flags/inline")).andExpect(content().string("false"));
    mockMvc.perform(get("/test-flags/getter").param("featureFlag", "FEATURE:true"))
        .andExpect(content().string("true"));
  }

  @Test
  void methodAndControllerGatingUseOverridesBeforeCheckingFlags() throws Exception {
    for (String path : new String[] {"/test-flags/method", "/test-flags/class"}) {
      mockMvc.perform(get(path)).andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.message").value("Feature is not available: FEATURE"));
      mockMvc.perform(get(path).param("featureFlag", "FEATURE:true")).andExpect(status().isOk());
      mockMvc.perform(get(path).param("featureFlag", "FEATURE:false")).andExpect(status().isNotFound());
    }
  }

  @Test
  void methodAnnotationTakesPrecedenceOverControllerAnnotation() throws Exception {
    mockMvc.perform(get("/test-flags/method-precedence")).andExpect(status().isOk());
  }

  @Test
  void malformedUnknownAndDuplicateOverridesReturnBadRequest() throws Exception {
    for (String parameter : new String[] {"FEATURE:invalid", "UNKNOWN:true", "FEATURE"}) {
      mockMvc.perform(get("/test-flags/method").param("featureFlag", parameter))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.timestamp").exists());
    }
    mockMvc.perform(get("/test-flags/inline").param("featureFlag", "FEATURE:true", "FEATURE:false"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void unimplementedFeatureReturnsServerError() throws Exception {
    mockMvc.perform(get("/test-flags/unimplemented")).andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Feature has not been implemented: null"));
  }

  @Nested
  @TestPropertySource(properties = "feature-flags.is-feature-enabled=true")
  class ConfiguredOn {
    @Test
    void overrideCanDisableAnEnabledFeature() throws Exception {
      mockMvc.perform(get("/test-flags/method")).andExpect(status().isOk());
      mockMvc.perform(get("/test-flags/method").param("featureFlag", "FEATURE:false"))
          .andExpect(status().isNotFound());
      mockMvc.perform(get("/test-flags/inline").param("featureFlag", "FEATURE:false"))
          .andExpect(content().string("false"));
      mockMvc.perform(get("/test-flags/method")).andExpect(status().isOk());
    }
  }

  @Nested
  @TestPropertySource(properties = "feature-flags.request-overrides-enabled=false")
  class OverridesDisabled {
    @Test
    void overridesReturnForbiddenButNormalRequestsContinue() throws Exception {
      mockMvc.perform(get("/test-flags/inline").param("featureFlag", "FEATURE:true"))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.status").value(403));
      mockMvc.perform(get("/test-flags/inline")).andExpect(status().isOk());
    }
  }

  @RestController
  static class MethodController {
    private final FeatureFlagsConfig flags;

    MethodController(FeatureFlagsConfig flags) {
      this.flags = flags;
    }

    @GetMapping("/test-flags/inline")
    public boolean inline() {
      return flags.isEnabled(Feature.FEATURE);
    }

    @GetMapping("/test-flags/getter")
    public boolean getter() {
      return flags.getIsFeatureEnabled();
    }

    @GetMapping("/test-flags/method")
    @RequiresFeatureFlag(Feature.FEATURE)
    public String gated() {
      return "enabled";
    }

    @GetMapping("/test-flags/unimplemented")
    public String unimplemented() {
      throw new FeatureNotImplementedRuntimeException(null);
    }
  }

  @RestController
  @RequiresFeatureFlag(Feature.FEATURE)
  static class ClassController {
    @GetMapping("/test-flags/class")
    public String gated() {
      return "enabled";
    }

    @GetMapping("/test-flags/method-precedence")
    @RequiresFeatureFlag({})
    public String methodPrecedence() {
      return "enabled";
    }
  }
}
