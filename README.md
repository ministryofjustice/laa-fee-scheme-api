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

### Feature flags

The sample implements the configuration approach investigated in
[LFSP-562](https://dsdmoj.atlassian.net/browse/LFSP-562). It has three stages:

```text
Helm value
  -> environment variable
  -> application.yml
  -> FeatureFlagProperties
  -> FeatureFlagService
       -> inline if statement
       -> @RequiresFeatureFlag endpoint check

Non-production test request
  -> ?featureFlag=<flag-key>:<true|false>
  -> request-scoped override
  -> FeatureFlagService
```

1. **Configure the value.** Helm sets a non-secret environment variable. `application.yml`
   maps it into `FeatureFlagProperties`, with missing flags defaulted to `false`.
2. **Evaluate the value.** Application code asks `FeatureFlagService`; it does not read
   environment variables directly.
3. **Choose the behaviour.** Code either uses a normal `if` statement or adds
   `@RequiresFeatureFlag` to a controller or method. The interceptor returns `404 Not Found`
   before a disabled endpoint runs.

When request overrides are enabled, `FeatureFlagService` uses the value supplied for the
current request before falling back to the configured environment value. This lets automated
tests exercise both states without changing shared configuration or restarting pods:

```text
GET /some-endpoint?featureFlag=example-feature:true
GET /some-endpoint?featureFlag=example-feature:false
```

The `featureFlag` parameter may be repeated when a request needs to override more than one
flag. Unknown flags, invalid booleans and duplicate overrides return `400 Bad Request`.
Supplying any override where the capability is disabled returns `403 Forbidden`.

Request overrides are enabled for development, UAT and staging deployments and explicitly
disabled in production. For a locally run service, set
`FEATURE_FLAG_REQUEST_OVERRIDES_ENABLED=true`. Overrides are stored on the current servlet
request and never change the configured value or another request.

The annotation/interceptor path is optional. A feature that only needs inline branching uses
`FeatureFlagService` and does not interact with the endpoint-gating classes.

Most classes are one-time infrastructure:

| Part | Purpose | Changed for each new flag? |
| --- | --- | --- |
| `FeatureFlag` | Registry of recognised, stable flag keys | Yes |
| `application.yml` and Helm values | Environment-specific state | Yes |
| Calling service or controller | Chooses what the flag controls | Yes |
| `FeatureFlagProperties` | Binds and validates configured values | No |
| `FeatureFlagService` | Resolves request overrides before configured values | No |
| Request override interceptor | Validates and stores non-production test overrides | No |
| `RequiresFeatureFlag` and `FeatureFlagInterceptor` | Reusable endpoint gating | No |
| `FeatureFlagConfiguration` and MVC configurer | Spring bean and interceptor wiring | No |
| `FeatureDisabledException` and exception handler | Consistent disabled-endpoint response | No |

`FeatureFlagService` centralises the lookup so inline checks and endpoint gating use the same
configuration and tests can replace it with a mock.

`EXAMPLE_FEATURE` is included only to demonstrate the pattern in this draft. To add a real flag:

1. Add it to `FeatureFlag` with a stable kebab-case key.
2. Add its environment-backed value to `application.yml`, `_envs.tpl` and `values.yaml`.
3. Use `FeatureFlagService` or `@RequiresFeatureFlag` where the behaviour changes.

Use `FeatureFlagService` for inline branching:

```java
if (featureFlagService.isEnabled(FeatureFlag.EXAMPLE_FEATURE)) {
  return newBehaviour();
}
return existingBehaviour();
```

Use `@RequiresFeatureFlag` for a whole controller or controller method:

```java
@RequiresFeatureFlag(FeatureFlag.EXAMPLE_FEATURE)
public ResponseEntity<Void> exampleEndpoint() {
  return ResponseEntity.ok().build();
}
```

`@WebMvcTest` does not load the regular feature flag configuration automatically. Import
`FeatureFlagConfiguration` in controller slice tests and mock or configure `FeatureFlagService`
so both flag states are tested.

Changing a Helm value rolls the pods because environment variables are read at application
startup. Changing a Kubernetes secret would have the same limitation: it would not update the
value in an already-running Java process. The request override avoids that restart for tests
without changing shared state. The sample uses non-secret Helm values because flag states are
not sensitive. Real flags should have an owner and removal plan, and should be removed after
rollout.

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
