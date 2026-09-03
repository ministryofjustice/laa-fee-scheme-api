#!/usr/bin/env bash

set -euo pipefail

KUBE_NAMESPACE="$1"
SERVICE_NAME="$2"
LOCAL_PORT="$3"
REMOTE_PORT="$4"
LOG_FILE="$5"

if [ -z "$KUBE_NAMESPACE" ] || [ -z "$SERVICE_NAME" ] || [ -z "$LOCAL_PORT" ] || [ -z "$REMOTE_PORT" ] || [ -z "$LOG_FILE" ]; then
  echo "Usage: $0 <namespace> <service-name> <local-port> <remote-port> <log-file>" >&2
  exit 1
fi

kubectl port-forward -n "$KUBE_NAMESPACE" "service/$SERVICE_NAME" "${LOCAL_PORT}:${REMOTE_PORT}" > "$LOG_FILE" 2>&1 &
CHILD_PID=$!

cleanup() {
  kill "$CHILD_PID" >/dev/null 2>&1 || true
  wait "$CHILD_PID" >/dev/null 2>&1 || true
}

trap cleanup EXIT INT TERM
wait "$CHILD_PID"
