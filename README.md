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

### Run application via intellij

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
- [Testcontainers](https://testcontainers.com/) - used to provide a PostgreSQL container for the integration tests.


