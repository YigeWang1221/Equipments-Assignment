#!/usr/bin/env bash
set -euo pipefail

# Starts a dedicated MySQL 8 instance on port 3307 for local development.
# It does not touch any existing MySQL installation on port 3306.

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DATA_DIR="${EQUIP_MYSQL_DATA_DIR:-/Users/eric/tmp/equip-mysql-data}"
RUN_DIR="${EQUIP_MYSQL_RUN_DIR:-/Users/eric/tmp/equip-mysql-run}"
PORT="${EQUIP_MYSQL_PORT:-3307}"
SOCKET="$RUN_DIR/mysql.sock"
MYSQLD="${MYSQLD_BIN:-/opt/homebrew/opt/mysql@8.4/bin/mysqld}"
MYSQL="${MYSQL_BIN:-/opt/homebrew/opt/mysql@8.4/bin/mysql}"

if [[ ! -x "$MYSQLD" ]]; then
  echo "mysqld not found at $MYSQLD" >&2
  echo "Install MySQL 8 or set MYSQLD_BIN / MYSQL_BIN." >&2
  exit 1
fi

mkdir -p "$DATA_DIR" "$RUN_DIR"

if [[ ! -d "$DATA_DIR/mysql" ]]; then
  echo "Initializing MySQL data directory at $DATA_DIR ..."
  "$MYSQLD" --initialize-insecure --datadir="$DATA_DIR"
fi

if [[ -S "$SOCKET" ]] && "$MYSQL" --socket="$SOCKET" -uroot -e "SELECT 1" >/dev/null 2>&1; then
  echo "MySQL already running on socket $SOCKET"
else
  echo "Starting MySQL on port $PORT ..."
  nohup "$MYSQLD" \
    --datadir="$DATA_DIR" \
    --port="$PORT" \
    --socket="$SOCKET" \
    --pid-file="$RUN_DIR/mysql.pid" \
    --log-error="$RUN_DIR/mysql.err" \
    --mysqlx=0 \
    >"$RUN_DIR/stdout.log" 2>&1 &
  for _ in $(seq 1 30); do
    if "$MYSQL" --socket="$SOCKET" -uroot -e "SELECT 1" >/dev/null 2>&1; then
      break
    fi
    sleep 1
  done
fi

"$MYSQL" --socket="$SOCKET" -uroot <<'SQL'
CREATE USER IF NOT EXISTS 'equip'@'%' IDENTIFIED BY 'equip123456';
CREATE USER IF NOT EXISTS 'equip'@'localhost' IDENTIFIED BY 'equip123456';
CREATE DATABASE IF NOT EXISTS equip_management DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON equip_management.* TO 'equip'@'%';
GRANT ALL PRIVILEGES ON equip_management.* TO 'equip'@'localhost';
FLUSH PRIVILEGES;
SQL

if [[ "${EQUIP_MYSQL_SKIP_SEED:-0}" != "1" ]]; then
  echo "Importing schema and demo data ..."
  "$MYSQL" --socket="$SOCKET" -uroot < "$ROOT_DIR/sql/schema.sql"
  "$MYSQL" --socket="$SOCKET" -uroot < "$ROOT_DIR/sql/data.sql"
fi

echo "MySQL ready: 127.0.0.1:$PORT  database=equip_management  user=equip  password=equip123456"
echo "Run backend with: cd \"$ROOT_DIR\" && mvn spring-boot:run"
