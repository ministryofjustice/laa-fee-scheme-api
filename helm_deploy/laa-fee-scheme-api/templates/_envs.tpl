{{/*
Environment variables for service containers
*/}}
{{- define "laa-fee-scheme-api.env-vars" }}
env:
  - name: DB_SERVER
    valueFrom:
      secretKeyRef:
        name: rds-mssql-instance-output
        key: rds_instance_endpoint
  - name: DB_USER
    valueFrom:
      secretKeyRef:
        name: rds-mssql-instance-output
        key: database_username
  - name: DB_PASSWORD
    valueFrom:
      secretKeyRef:
        name: rds-mssql-instance-output
        key: database_password
{{- end -}}
