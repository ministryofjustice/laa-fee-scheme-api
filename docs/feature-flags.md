# Feature flag investigation (LFSP-562)

## Decision

Use a small, typed `FeatureFlagService` backed by Spring Boot configuration for
short-lived, binary behavioural flags.

This follows existing LAA patterns, is simple to test, and allows incomplete
features to be merged without release branches. Application code should depend
on the service rather than reading configuration properties directly so that a
dynamic provider can be introduced later without changing every call site.

Environment variables do not provide true live flag changes:

- Kubernetes ConfigMaps and Secrets consumed as environment variables are only
  read when a pod starts.
- Spring Boot configuration properties are bound when the application context
  starts.
- Changing a GitHub Environment variable or secret does not update an already
  running deployment.

The simple option therefore needs a rolling pod restart, although it does not
need a new application build or image. If changing flags without restarting
pods is a hard requirement, use a dynamic provider such as Flipt instead.

Feature flags are not sensitive values. Store flag values in GitHub Environment
Variables and Kubernetes ConfigMaps, not Secrets. Credentials used to access a
flag provider, if required, remain Secrets.

## Options considered

| Option | Runtime change | Advantages | Costs and limitations | Recommendation |
| --- | --- | --- | --- | --- |
| Spring Boot configuration properties backed by environment variables or a ConfigMap | Requires a pod rollout | Small implementation, no new service, follows LAA precedent, fail-closed defaults | No live refresh, no targeting, limited audit trail unless changes are made through a controlled workflow | Use initially |
| `@ConditionalOnProperty` | Requires an application restart | Useful for selecting optional bean implementations at startup | Not suitable for inline request-time decisions and not dynamic | Use only for bean wiring |
| Database-backed flags | Can be dynamic | Uses infrastructure already owned by the service, can support audit history | Requires an administration path, authorisation, caching, failure semantics, metrics, and cleanup | Do not build unless requirements justify it |
| Flipt or another dedicated provider | Dynamic, normally refreshed by a client cache | Central management, per-environment state, targeting, auditability, no application rollout | Provider dependency, onboarding/network work, operational ownership | Evaluate if zero-restart toggles are required |

The Ministry of Justice currently operates an
[HMPPS Flipt service](https://github.com/ministryofjustice/hmpps-feature-flags).
There is a Java client precedent in
[`hmpps-electronic-monitoring-crime-matching-api`](https://github.com/ministryofjustice/hmpps-electronic-monitoring-crime-matching-api).
That client evaluates locally from periodically refreshed state and defaults
disabled when the provider cannot supply a flag. LAA access and network
suitability would need to be confirmed before relying on the shared service.

## Existing LAA implementations

The
[Amend `FeatureFlagsConfig`](https://github.com/ministryofjustice/laa-amend-a-claim/blob/a1dab2a29334bdb5c735bd20a52653d338c059d2/src/main/java/uk/gov/justice/laa/amend/claim/config/FeatureFlagsConfig.java)
uses `@ConfigurationProperties` with environment-backed values that default to
false.

Amend also provides:

- a `@RequiresFeatureFlag` annotation for controllers and controller methods;
- a Spring MVC interceptor that checks the annotation before invoking a
  handler;
- a 404 response while a gated controller is unavailable;
- tests for class-level flags, method-level flags, unannotated handlers, and
  disabled flags.

[`laa-submit-a-bulk-claim`](https://github.com/ministryofjustice/laa-submit-a-bulk-claim/blob/main/laa-submit-a-bulk-claim-ui/src/main/java/uk/gov/justice/laa/bulkclaim/controller/ClaimDetailController.java)
also demonstrates inline flagging. It injects feature configuration and chooses
between the old and new implementation with a conditional statement.

Fee Scheme API should reuse these ideas without exposing mutable configuration
objects throughout the application.

## Proposed application design

### Stable service boundary

Application code should use a small interface:

```java
public interface FeatureFlagService {
  boolean isEnabled(Feature feature);

  void requireEnabled(Feature feature);
}
```

Flags should be represented by an enum so references are searchable and
compile-time checked:

```java
public enum Feature {
  EXAMPLE_NEW_BEHAVIOUR
}
```

The initial implementation can be backed by an immutable
`@ConfigurationProperties` record. Each flag must have an explicit false
default in `application.yml`:

```yaml
feature-flags:
  example-new-behaviour: ${FEATURE_EXAMPLE_NEW_BEHAVIOUR:false}
```

False defaults prevent a missing environment value from accidentally exposing
unfinished behaviour.

### Inline behaviour

Use inline checks where the same endpoint must choose old or new behaviour:

```java
if (featureFlagService.isEnabled(Feature.EXAMPLE_NEW_BEHAVIOUR)) {
  return newBehaviour();
}
return existingBehaviour();
```

This is preferable to conditional bean creation when the decision is made for
each request or when a safe fallback must remain available.

### Controller-level behaviour

For a wholly new endpoint, `requireEnabled` can be called at the controller or
service boundary. If repeated controller gating becomes common, add an
annotation/interceptor based on the Amend implementation.

The disabled response must be chosen deliberately:

- return 404 for an unpublished endpoint that should not be advertised;
- fall back to existing behaviour when replacing an implementation;
- return the standard service-unavailable or domain error for a kill switch
  that cannot safely fall back.

Do not return a successful empty response when a flag blocks required
behaviour.

### Flag lifecycle

Every flag should have:

- an owner;
- a documented default;
- an expected removal condition or date;
- a cleanup ticket;
- metrics or structured logging sufficient to identify the active path.

Permanent business rules are not feature flags and should be modelled in the
domain.

## Environment configuration and deployment

This repository centralises container environment variables in
`helm_deploy/laa-fee-scheme-api/templates/_envs.tpl` and already has
environment-specific Helm values for dev, UAT, staging, and production.

The simple implementation should:

1. Define flag values in a chart-managed ConfigMap.
2. Import each value into the deployment as a `FEATURE_*` environment variable.
3. Default every flag to false in the base values.
4. Override flags explicitly per environment.
5. Add a ConfigMap checksum to the pod template, or explicitly restart the
   deployment, so a Helm flag change rolls the pods.
6. Provide an environment-protected manual GitHub Actions workflow that changes
   flags without rebuilding the application image.

The workflow should record the environment, flag, value, actor, and rollout
result. Production changes should use the repository's existing GitHub
Environment approval controls.

Preview releases share the dev namespace. A fixed ConfigMap name would make all
previews share the same flags, so preview flags should be release-scoped or
passed directly as release-specific Helm values.

Kubernetes mounted ConfigMap files are eventually refreshed, but changing the
file alone would not rebind normal Spring Boot configuration properties.
Adding a watcher and refresh mechanism is more complex than the initial design
and less capable than a dedicated flag provider.

## Testing strategy

### Configuration tests

- Verify all flags default to false.
- Verify environment-style overrides bind to the expected property.
- Fail application startup for invalid flag values rather than silently using a
  fallback.

### Unit tests

- Test both enabled and disabled branches for each inline flag.
- Test the safe fallback separately from the new behaviour.
- Mock `FeatureFlagService`, not Spring configuration internals.
- Test the provider failure policy. A dynamic provider should fail closed for
  unfinished features and emit an explicit log/metric.

### Controller tests

- Prove disabled new endpoints return the agreed response.
- Prove enabled endpoints reach the handler.
- If an annotation/interceptor is added, test class-level, method-level,
  unannotated, and non-handler requests.

### Integration and end-to-end tests

- Set flag values explicitly in integration-test configuration.
- Do not inherit a developer's local environment values.
- For an existing behaviour being replaced, keep the current path as the normal
  test default and add a focused suite for the new path.
- For a wholly new feature, keep the normal default false and enable it only in
  tests for that feature.
- Avoid testing every possible combination of unrelated flags. Add combination
  tests only where flags interact.
- Add a deployment smoke check that confirms the intended environment flag
  state and behaviour.

This repository enforces branch coverage and mutation testing. Both sides of
inline conditionals must therefore be tested, even though configuration classes
are currently excluded from coverage.

## Backdated fee codes

Backdated fee codes are not a normal boolean feature flag.

The current calculation flow already uses the claim's relevant date and the
`fee_schemes.valid_from` / `valid_to` period:

- `FeeDataService` loads all fee records for the requested fee code.
- `AbstractFeeValidationService` filters those records by the claim date.
- The most recent valid scheme is selected.

This means a fee code or rate deployed later with a backdated scheme start date
can calculate historical claims correctly after the data is available. Claims
before the effective date remain invalid.

A current-time boolean must not replace this date-based rule. If a global flag
were later disabled or removed, recalculating the same historical claim could
produce a different result.

### Current discoverability gap

The fee-code discovery path is not date-aware:

- `/api/v1/fee-codes/{areaOfLaw}` accepts only an area of law;
- `FeeCodesService` returns globally mapped fee codes;
- `fee_code_information` and `fee_category_mapping` do not have availability
  periods.

Loading a new fee code therefore makes it immediately visible in discovery and
details responses, even if it has been preloaded before its operational launch.

### Recommended model

Keep two separate concepts:

1. **Legal effective period** - the dates on which a fee code/rate is valid for
   a claim. Continue to derive this from fee schemes and the claim date.
2. **Operational visibility** - whether consumers should currently be offered
   the code. Model this separately only when codes must be preloaded before
   launch.

A future fee-code API version should accept an `asAt` or claim date and return
only codes that have a valid fee record for that date. This can be derived by
joining fee mappings to fee records and fee schemes.

If preloading before public availability is required, add explicit publication
metadata such as `available_from` rather than relying on a permanent feature
flag. A short-lived behavioural flag may gate the new discovery behaviour, but
calculation eligibility must remain date-driven.

The result is deterministic:

- historical claims continue to use the legally effective fee code;
- future claims cannot use it before its effective date;
- removing a rollout flag does not change historical calculations;
- discovery can be controlled independently from calculation.

## Acceptance criteria coverage

| Acceptance criterion | Finding |
| --- | --- |
| Inline feature flagging using conditional statements | Use `FeatureFlagService.isEnabled` and test both paths; Submit a Bulk Claim provides an existing example |
| Configuration setup | Spring Boot configuration properties with false defaults, Helm ConfigMap values, and environment-specific overrides |
| Testing it works as intended | Configuration binding, unit branch, MVC gate, integration profile, provider failure, and deployment smoke tests |
| Test-suite impact | Explicit per-test flag state, no unrelated combination matrix, and coverage of both conditional branches |
| Environment control | ConfigMap/GitHub Environment Variables plus a controlled rollout; environment variables alone cannot hot-refresh pods |
| Backdated fee codes | Keep calculation eligibility effective-dated and separate it from operational visibility |

## Proposed follow-on work

1. Implement a proof of concept with one inline flag and one controller-level
   flag, including configuration, unit, and MVC tests.
2. Add Helm ConfigMap wiring and an audited manual rollout workflow.
3. Design a date-aware fee-code discovery API and any publication metadata
   needed for preloaded codes.
4. Confirm whether a rolling restart is operationally acceptable. Only pursue
   Flipt onboarding or another dynamic provider if true zero-restart toggles are
   required.
