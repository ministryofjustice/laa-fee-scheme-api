# Repository guidance for coding agents

This file applies to the whole repository. A more specific `AGENTS.md` in a
subdirectory overrides it for that subtree. Treat `README.md`, Gradle files,
scripts, and CI workflows as the source of truth; update this file in the same
change whenever those workflows change.

## Repository map

- `scheme-api/open-api-specification.yml`: source API contract.
- `scheme-api/generated/`: generated Java sources; never edit or commit these.
- `scheme-service/src/main/java/`: Spring Boot application code.
- `scheme-service/src/test/`: unit and MVC slice tests.
- `scheme-service/src/integrationTest/`: Spring and Testcontainers integration
  tests.
- `scheme-service/src/main/resources/db/`: Flyway schema migrations and
  repeatable reference-data scripts.
- `scheme-service/src/regressionTest/`: TypeScript/Cucumber API regression
  tests.
- `helm_deploy/`: Helm chart and environment-specific configuration.
- `.github/workflows/`: CI, security scanning, preview, and deployment
  workflows.

## Toolchain

- Use Java 25 and the checked-in Gradle wrapper (`./gradlew`), not a system
  Gradle installation.
- Docker is required for Testcontainers, the full build, and the local
  PostgreSQL/application stack.
- Node.js and npm are required only for the Cucumber regression suite.
- Follow the setup and local-run instructions in `README.md`; do not duplicate
  local credentials in tracked files.

## Implementation rules

- Make the smallest coherent change and follow patterns in neighbouring
  production code and tests.
- For API contract changes, edit `scheme-api/open-api-specification.yml`.
  `compileJava` regenerates the interfaces and models. Do not patch generated
  sources.
- Add a new numbered Flyway migration for schema changes; do not rewrite an
  already-applied `V...__` migration. Change repeatable `R__` scripts only when
  their data should intentionally be reapplied.
- Follow the feature-flag wiring documented in `README.md`: update the enum,
  typed configuration, Spring configuration, Helm values/environment mapping,
  application logic, and tests for both enabled and disabled states. Do not
  read feature-flag environment variables directly in application code.
- Put fast behaviour tests in `src/test` and database or full Spring context
  coverage in `src/integrationTest`. Prefer parameterized tests for behaviour
  matrices, matching existing tests.
- Java code follows the Checkstyle configuration in `config/checkstyle`.
  Resolve violations rather than adding suppressions unless there is a
  documented exceptional case.
- Do not lower JaCoCo or PIT thresholds to make CI pass. Add or improve tests
  instead. Current thresholds live in `scheme-service/build.gradle`.

## Commands

Run commands from the repository root.

| Purpose | Command | Notes |
| --- | --- | --- |
| Unit tests | `./gradlew test` | Minimum test command in the PR checklist. |
| Targeted unit test | `./gradlew :scheme-service:test --tests 'fully.qualified.TestClass'` | Add a method pattern after the class when useful. |
| Integration tests | `./gradlew :scheme-service:integrationTest` | Requires Docker for Testcontainers. |
| Targeted integration test | `./gradlew :scheme-service:integrationTest --tests 'fully.qualified.TestClass'` | Prefer this while iterating. |
| Checkstyle | `./gradlew checkstyleMain checkstyleTest checkstyleIntegrationTest` | Runs matching tasks across subprojects. |
| Full verification | `./gradlew clean build` | Mirrors the main CI build and requires Docker. |
| Mutation tests | `./gradlew :scheme-service:pitest` | Enforced separately in CI; can take substantially longer. |
| Regenerate API sources | `./gradlew :scheme-api:openApiGenerate` | Output is ignored and should not be committed. |
| Validate regression scenarios | `./run-regression-tests.sh --local --dry-run` | Installs npm dependencies on first use; makes no API calls. |

For live local or deployed-environment regression runs, use the documented
`.env.local` or `.env` flow in `README.md`. Never commit either file.

## Verification expectations

- Start with the smallest relevant test, then expand based on the affected
  surface.
- Run the full build for Java, API contract, database, build configuration, or
  cross-module changes when the local environment supports Docker.
- For fee-calculation changes, cover success, validation, boundary, and
  feature-flag cases as applicable.
- Review generated API changes through compilation and tests rather than by
  committing generated files.
- Before handing off, inspect the diff against `main` and state clearly which
  checks were run and which were not.

## Secrets and safe execution

- Never commit credentials, tokens, private URLs, customer data, or environment
  secrets. Keep values in ignored files such as `application-local.yml`,
  `.env`, and `.env.local`.
- Placeholder values in examples and `docker-compose.yml` must remain
  non-sensitive.
- Repository inspection, focused edits, local builds/tests, and repo-scoped
  Docker containers are routine operations.
- Obtain human approval before publishing artifacts, deploying, changing
  shared cloud/Kubernetes resources, writing to a shared database, or running
  commands with irreversible external effects.
- Obtain human approval before destructive local operations such as force
  pushes, hard resets, deleting branches/worktrees, or deleting Docker volumes.
  Never use destructive commands merely to bypass a failing check.

## Pull requests and maintenance

- Link the Jira issue, explain what changed and why, and follow
  `.github/PULL_REQUEST_TEMPLATE.md`.
- Keep commits and pull requests focused; avoid mixing formatting-only changes
  with functional changes.
- Repository maintainers own this file. Update it in the same pull request when
  commands, layout, guardrails, or validation expectations change.
