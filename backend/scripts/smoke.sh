#!/usr/bin/env bash
set -euo pipefail

BASE="${BASE_URL:-http://localhost:8090}"
USERNAME="${USERNAME:-teacher01}"
PASSWORD="${PASSWORD:-123456}"

echo "== health =="
curl -fsS "$BASE/actuator/health" | jq .

echo "== login =="
LOGIN=$(curl -fsS -X POST "$BASE/api/v1/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")
echo "$LOGIN" | jq .
TOKEN=$(echo "$LOGIN" | jq -r '.data.token')

AUTH=(-H "Authorization: Bearer $TOKEN")

echo "== classes =="
curl -fsS "$BASE/api/v1/classes" "${AUTH[@]}" | jq '.data | length'

echo "== class 1 works =="
curl -fsS "$BASE/api/v1/works?classId=1" "${AUTH[@]}" | jq '.data[] | {id,workName,status,statusName}'

echo "== equipments =="
curl -fsS "$BASE/api/v1/equipments" "${AUTH[@]}" | jq '.data[] | {id,name,totalNumber,usedNumber,availableNumber}'

echo "== borrows =="
curl -fsS "$BASE/api/v1/borrows" "${AUTH[@]}" | jq '.data | length'

echo "== stats =="
curl -fsS "$BASE/api/v1/stats/classes/1" "${AUTH[@]}" | jq .
curl -fsS "$BASE/api/v1/stats/equipments" "${AUTH[@]}" | jq .

echo "Smoke test finished."
