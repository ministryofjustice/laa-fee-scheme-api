package uk.gov.justice.laa.fee.scheme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.fee.scheme.featureflag.FeatureFlag;
import uk.gov.justice.laa.fee.scheme.featureflag.FeatureFlagService;
import uk.gov.justice.laa.fee.scheme.featureflag.RequiresFeatureFlag;

/**
 * Non-production endpoints for demonstrating the feature flag test pattern.
 */
@RestController
@RequestMapping("/feature-flags/example-feature")
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "feature-flags",
    name = "request-overrides-enabled",
    havingValue = "true")
public class FeatureFlagExampleController {

  private final FeatureFlagService featureFlagService;

  /**
   * Returns the effective state after applying any request override.
   *
   * @return the example feature state
   */
  @GetMapping
  public ResponseEntity<FeatureFlagState> getState() {
    return ResponseEntity.ok(currentState());
  }

  /**
   * Demonstrates controller gating using the effective feature state.
   *
   * @return the enabled example feature state
   */
  @GetMapping("/gated")
  @RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
  public ResponseEntity<FeatureFlagState> getGatedState() {
    return ResponseEntity.ok(currentState());
  }

  private FeatureFlagState currentState() {
    return new FeatureFlagState(
        FeatureFlag.EXAMPLE_FEATURE.key(),
        featureFlagService.isEnabled(FeatureFlag.EXAMPLE_FEATURE));
  }

  /**
   * Effective state returned by the demonstration endpoints.
   *
   * @param featureFlag stable feature flag key
   * @param enabled effective value for this request
   */
  public record FeatureFlagState(String featureFlag, boolean enabled) {
  }
}
