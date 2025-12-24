#!/bin/bash
set -e

# Create multiple databases
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "$USER_DB_NAME";
    CREATE DATABASE "$BILLING_DB_NAME";
EOSQL
