#!/bin/bash
set -e

# Create multiple databases
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "$USER_DB_NAME";
    CREATE DATABASE "$BILLING_DB_NAME";
EOSQL

# Configure replication access
cat >> "$PGDATA/pg_hba.conf" <<EOF
# Allow replication connections
host    replication     all             0.0.0.0/0               trust
host    all             all             0.0.0.0/0               trust
EOF

# Reload PostgreSQL configuration
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -c "SELECT pg_reload_conf();"
