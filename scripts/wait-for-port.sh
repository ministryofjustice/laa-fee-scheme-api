#!/usr/bin/env bash

set -euo pipefail

PORT="${1:-}"
LABEL="${2:-port}"

if [ -z "$PORT" ]; then
  echo "Usage: $0 <port> [label]" >&2
  exit 1
fi

for i in {1..30}; do
  if (echo > "/dev/tcp/127.0.0.1/${PORT}") >/dev/null 2>&1; then
    echo "Port ${PORT} (${LABEL}) is ready"
    exit 0
  fi
  sleep 2
done

echo "Timed out waiting for ${LABEL} on port ${PORT}" >&2
exit 1
