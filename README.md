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

#### In GitHub Actions

The `PR regression tests` workflow (`.github/workflows/pr-regression-tests.yml`) runs automatically on active PR commits (`pull_request`: `synchronize`/`opened`/`reopened`) using a local Dockerised stack.

When preview deployment runs (`.github/workflows/deploy-preview.yml`), regression tests also run against the deployed preview URL.

The `Regression tests` workflow (`.github/workflows/regression-tests.yml`) is for deployed environments and manual preview/dev/uat/staging reruns.

`PR regression tests` can also be run manually from **Actions** for local Docker reruns.

`Regression tests` can be run manually from **Actions** against:
- preview environments
- development
- uat
- staging

The main deployment pipeline (`.github/workflows/deploy.yml`) now runs regression tests after each deployment stage:
- after development deploy
- after uat deploy
- after staging deploy

For development/uat/staging, set environment-scoped configuration:
- `vars.FSP_API_BASE_URL` (required)
- `vars.APP_HEALTHCHECK_URL` (optional, defaults to `<FSP_API_BASE_URL>/actuator/health`)
- `secrets.FSP_API_TOKEN` (required)

For preview, choose `preview` and provide `preview_base_url` when triggering the workflow (requires `secrets.FSP_API_TOKEN` in the `dev` GitHub Environment).

Each regression workflow run uploads Cucumber reports from `scheme-service/src/regressionTest/reports` as a GitHub Actions artifact.

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
