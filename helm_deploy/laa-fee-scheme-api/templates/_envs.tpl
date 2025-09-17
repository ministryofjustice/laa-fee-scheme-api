{{/*
Environment variables for service containers
*/}}
{{- define "laa-fee-scheme-api.env-vars" }}
env:
  - name: DB_SERVER
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: rds_instance_endpoint
  - name: DB_NAME
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_name
  - name: DB_USER
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_username
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_password
  - name: DATA_CLAIMS_EVENT_SERVICE_TOKEN
    valueFrom:
      secretKeyRef:
        name: fee-scheme-api-secrets
        key: DATA_CLAIMS_EVENT_SERVICE_TOKEN
  {{- if .Values.sentry.enabled }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry.dsn }}
  - name: SENTRY_ENVIRONMENT
    value: {{ .Values.sentry.environment }}
  {{ end -}}
{{- end -}}
