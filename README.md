# laa-fee-scheme-api
[![Ministry of Justice Repository Compliance Badge](https://github-community.service.justice.gov.uk/repository-standards/api/laa-fee-scheme-api/badge)](https://github-community.service.justice.gov.uk/repository-standards/laa-fee-scheme-api)

## Overview

The project uses the `laa-spring-boot-gradle-plugin` Gradle plugin which provides
sensible defaults for the following plugins:

- [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
- [Dependency Management](https://plugins.gradle.org/plugin/io.spring.dependency-management)
- [Jacoco](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- [Java](https://docs.gradle.org/current/userguide/java_plugin.html)
- [Maven Publish](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Spring Boot](https://plugins.gradle.org/plugin/org.springframework.boot)
- [Test Logger](https://github.com/radarsh/gradle-test-logger-plugin)
- [Versions](https://github.com/ben-manes/gradle-versions-plugin)

The plugin is provided by -  [laa-spring-boot-common](https://github.com/ministryofjustice/laa-spring-boot-common), where you can find
more information regarding setup and usage.

### Project Structure
Includes the following subprojects:

- `scheme-api` - OpenAPI specification used for generating API stub interfaces and documentation.
- `scheme-service` - REST API service with operations interfacing a JPA repository to a PostgreSQL database.

## Build And Run Application

### Build application
`./gradlew clean build`

### Run cucumber regression tests

The script has two modes. See `scheme-service/src/regressionTest/.env.example` for the required variables.

#### Against local code

1. Create `scheme-service/src/regressionTest/.env.local` with your local values:
   - `FSP_API_BASE_URL=http://localhost:8085`
   - `APP_HEALTHCHECK_URL=http://localhost:8185/actuator/health`
   - `FSP_API_TOKEN=token1234` (default local token from `application-local.yml`)
2. Ensure the API is running locally, then from the repository root run:

`./run-regression-tests.sh --local`

#### Against a deployed environment

1. Populate `scheme-service/src/regressionTest/.env` with the target environment values.
2. From the repository root run:

`./run-regression-tests.sh --environment`

#### Dry run (no API calls)

`./run-regression-tests.sh --local --dry-run`

### Run application via intellij

Update placeholders in docker-compose.yml

- Create 'postgres' container for database, do not create 'app' container

`docker compose up postgres -d`

- Edit SpringBoot Run configuration
- Active profile: local
- Environment variables: - DATA_CLAIMS_EVENT_SERVICE_TOKEN={someToken}

Create application-local.yml

```yaml
spring:
  application:
    name: LAA Fee Scheme

  datasource:
    url: jdbc:postgresql://localhost:5432/fee_scheme_test_db
    username: dev
    password: dev
    driver-class-name: org.postgresql.Driver

  flyway:
    url: jdbc:postgresql://localhost:5432/fee_scheme_test_db
    user: dev
    password: dev
    locations: classpath:db/migration,classpath:db/repeatable
    baseline-on-migrate: true
    schemas: fee_scheme
    default-schema: fee_scheme
    enabled: true

  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_schema: fee_scheme

feature-flags:
  is-feature-enabled: true
  request-overrides-enabled: false

logging:
  level:
    root: ERROR

sentry:
  dsn: ""
  environment: ""
```

### Run application via Docker

Update placeholders in docker-compose.yml

`docker compose up`

### Feature flags

Flags follow a direct Boolean configuration pattern, with request-scoped
testing overrides added:

```text
Helm values -> environment variables -> application.yml -> FeatureFlagsConfig
                                                          -> inline checks
                                                          -> @RequiresFeatureFlag
```

`Feature` is a plain enum: there are no separate string keys or configuration maps.
For the sample `Feature.FEATURE`, Helm configures:

```yaml
featureFlags:
  isFeatureEnabled: false
  requestOverridesEnabled: true
```

The template maps these to `IS_FEATURE_ENABLED` and
`FEATURE_FLAG_REQUEST_OVERRIDES_ENABLED`. `IS_FEATURE_ENABLED` defaults to
`true` when it is not supplied, while invalid or unknown Spring feature
configuration still fails startup. Local configuration and the integration test
configuration provide explicit values.

Inject `FeatureFlagsConfig` for inline branching:

```java
if (featureFlagsConfig.isEnabled(Feature.FEATURE)) {
  // New behaviour
} else {
  // Existing behaviour
}
```

The `getIsFeatureEnabled()` getter also evaluates request overrides.
`checkEnabled(Feature.FEATURE)` throws if disabled. To gate an endpoint:

```java
@RequiresFeatureFlag(Feature.FEATURE)
@GetMapping("/new-endpoint")
public ResponseEntity<?> newEndpoint() {
  // ...
}
```

The annotation accepts multiple enum values, all of which must be enabled. A
method annotation takes precedence over a controller annotation.
Disabled endpoints return `404`; unimplemented features referenced by Java code
return `500`, using the API's existing error response format.

#### Request overrides

Authenticated automated tests can override a flag for a single request using
the `featureFlag` **query parameter** and the exact enum name, not a separate
key:

```text
GET /some-endpoint?featureFlag=FEATURE:true
GET /some-endpoint?featureFlag=FEATURE:false
```

Repeat the parameter for different flags. Unknown enum names, malformed values,
invalid booleans and duplicate flags return `400`. Boolean values are
case-insensitive; enum names are case-sensitive. Overrides are validated before
endpoint gating and affect inline checks too. They never mutate shared
configuration and do not leak into subsequent or concurrent requests. Background
work without a servlet request uses the configured value.

Overrides are enabled in dev, preview, UAT and staging Helm values, and disabled
by default and in production. Attempts when disabled return `403`. Overrides do
not bypass authentication.

Note: `isFeatureEnabled` in the sample config is a placeholder/dummy value used to
show the pattern. It is not a real feature flag in production; it demonstrates the
shape of the configuration wiring and should be replaced with the real flag name
and boolean property for each feature you add.

Files to change when adding a new feature flag:

- `scheme-service/src/main/java/uk/gov/justice/laa/fee/scheme/config/features/Feature.java`
  - add the new enum constant, e.g. `NEW_FEATURE`
- `scheme-service/src/main/java/uk/gov/justice/laa/fee/scheme/config/FeatureFlagsConfig.java`
  - add the Boolean property and the switch case used by `isEnabled(...)`
- `scheme-service/src/main/resources/application.yml`
  - add the Spring property mapping, for example `new-feature: ${NEW_FEATURE:false}`
- `helm_deploy/laa-fee-scheme-api/templates/_envs.tpl`
  - map the Helm value to the environment variable
- `helm_deploy/laa-fee-scheme-api/values.yaml`
  - default config for the new flag
- `helm_deploy/laa-fee-scheme-api/values-dev*.yaml`, `values-uat.yaml`,
  `values-staging.yaml`, `values-prod.yaml`
  - set the per-environment value for the flag
- `scheme-service/src/main/java/uk/gov/justice/laa/fee/scheme/config/features/FeatureFlagInterceptor.java`
  - endpoint gating is already generic; the new enum value is used automatically
- `scheme-service/src/main/java/uk/gov/justice/laa/fee/scheme/config/features/FeatureFlagRequestOverrideInterceptor.java`
  - request overrides already work against enum names; the new enum constant is automatically valid
- Application code that uses the feature
  - check `FeatureFlagsConfig.isEnabled(Feature.NEW_FEATURE)` or mark the endpoint with `@RequiresFeatureFlag(Feature.NEW_FEATURE)`
- Tests for the flag
  - add feature on/off assertions in the relevant test classes or dedicated config tests

To add a flag, add an enum constant, a required `@NotNull Boolean` property and
its switch case in `FeatureFlagsConfig`, then wire its Helm values, environment
variable and Spring property. Add explicit test values and exercise both states.
Use `isEnabled` (or an override-aware getter), never read environment variables
directly. Test controllers demonstrate both approaches without adding demo
endpoints or changing existing fee calculations.

## Application Endpoints

### API Documentation

#### Swagger UI
- http://localhost:8085/swagger-ui/index.html
#### API docs (JSON)
- http://localhost:8085/v3/api-docs

### Actuator Endpoints
The following actuator endpoints have been configured:
- http://localhost:8185/actuator
- http://localhost:8185/actuator/health
- http://localhost:8185/actuator/metrics
- http://localhost:8185/actuator/prometheus

## Additional Information

### Authentication
The [LAA SpringBoot Authentication Starter](https://github.com/ministryofjustice/laa-spring-boot-common/blob/main/laa-spring-boot-starters/laa-spring-boot-starter-auth/README.md)
has been used to secure the application using token-based authentication.
To access the API endpoints, you need to include a valid token in the `Authorization` header of your HTTP requests.

### Libraries Used
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html) - used to provide various endpoints to help monitor the application, such as view application health and information.
- [Spring Boot Web](https://docs.spring.io/spring-boot/reference/web/index.html) - used to provide features for building the REST API implementation.
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/jpa.html) - used to simplify database access and interaction, by providing an abstraction over persistence technologies, to help reduce boilerplate code.
- [Springdoc OpenAPI](https://springdoc.org/) - used to generate OpenAPI documentation. It automatically generates Swagger UI, JSON documentation based on your Spring REST APIs.
- [Lombok](https://projectlombok.org/) - used to help to reduce boilerplate Java code by automatically generating common
  methods like getters, setters, constructors etc. at compile-time using annotations.
- [Flyway](https://www.red-gate.com/products/flyway/) - used to manage database migrations.
- [Sentry SDK](https://docs.sentry.io/platforms/java/) - used to capture application exception events at runtime, which can be monitored via the Sentry UI.
- [Testcontainers](https://testcontainers.com/) - used to provide a PostgreSQL container for the integration tests.
