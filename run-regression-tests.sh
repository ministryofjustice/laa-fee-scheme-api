#!/usr/bin/env bash

set -euo pipefail

usage() {
  echo "Usage: $0 <--local|--environment> [--dry-run]"
  echo ""
  echo "  Modes:"
  echo "    --local        Run against local code using .env.local"
  echo "    --environment  Run against a deployed environment using .env"
  echo ""
  echo "  Options:"
  echo "    --dry-run      Parse and validate scenarios without making API calls"
  exit 1
}

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REGRESSION_DIR="${ROOT_DIR}/scheme-service/src/regressionTest"
ENV_LOCAL_FILE="${REGRESSION_DIR}/.env.local"
ENV_FILE="${REGRESSION_DIR}/.env"

if [[ ! -d "${REGRESSION_DIR}" ]]; then
  echo "Regression test directory not found: ${REGRESSION_DIR}" >&2
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "npm is required to run regression tests." >&2
  exit 1
fi

MODE=""
DRY_RUN=""

for arg in "$@"; do
  case "${arg}" in
    --local)       MODE="local" ;;
    --environment) MODE="environment" ;;
    --dry-run)     DRY_RUN="true" ;;
    *) echo "Unknown argument: ${arg}" >&2; usage ;;
  esac
done

if [[ -z "${MODE}" ]]; then
  usage
fi

cd "${REGRESSION_DIR}"

if [[ ! -d node_modules ]]; then
   if [[ -f package-lock.json ]]; then
     npm ci
   else
     npm install
   fi
fi

if [[ "${DRY_RUN}" == "true" ]]; then
  exec npm run test:dry-run
fi

if [[ "${MODE}" == "local" ]]; then
  ENV_SOURCE_FILE="${ENV_LOCAL_FILE}"
  if [[ ! -f "${ENV_SOURCE_FILE}" ]]; then
    echo "Missing ${ENV_SOURCE_FILE}" >&2
    echo "Please create scheme-service/src/regressionTest/.env.local with your local environment values." >&2
    echo "See .env.example for the required variables." >&2
    exit 1
  fi
else
  ENV_SOURCE_FILE="${ENV_FILE}"
  if [[ ! -f "${ENV_SOURCE_FILE}" ]]; then
    echo "Missing ${ENV_SOURCE_FILE}" >&2
    echo "Please create scheme-service/src/regressionTest/.env with your target environment values." >&2
    echo "See .env.example for the required variables." >&2
    exit 1
  fi
fi

set -a
# shellcheck disable=SC1090
source "${ENV_SOURCE_FILE}"
set +a

BASE_URL="${FSP_API_BASE_URL:-http://localhost:8085}"
HEALTHCHECK_URL="${APP_HEALTHCHECK_URL:-http://localhost:8185/actuator/health}"

HTTP_STATUS="$(curl --silent --show-error --output /dev/null --write-out "%{http_code}" --max-time 5 "${HEALTHCHECK_URL}" || true)"
if [[ "${HTTP_STATUS}" != "200" ]]; then
  if [[ "${HTTP_STATUS}" == "000" ]]; then
    echo "Cannot connect to API health endpoint: ${HEALTHCHECK_URL}" >&2
  else
    echo "API health endpoint returned HTTP ${HTTP_STATUS}: ${HEALTHCHECK_URL}" >&2
  fi
  echo "Check FSP_API_BASE_URL/APP_HEALTHCHECK_URL in ${ENV_SOURCE_FILE} and ensure the API is running." >&2
  exit 1
fi

echo "Running regression tests against ${BASE_URL}"
exec npm run test
