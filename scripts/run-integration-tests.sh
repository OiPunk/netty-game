#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"

cleanup() {
  docker compose -f "$COMPOSE_FILE" --profile integration down -v || true
}

trap cleanup EXIT

docker compose -f "$COMPOSE_FILE" --profile integration up -d mysql-it redis-it

for i in {1..60}; do
  if docker compose -f "$COMPOSE_FILE" --profile integration exec -T mysql-it mysqladmin ping -h 127.0.0.1 -uroot -proot_pass --silent >/dev/null 2>&1; then
    break
  fi

  if [[ "$i" -eq 60 ]]; then
    echo "MySQL did not become ready in time" >&2
    exit 1
  fi

  sleep 2
done

mvn -B -f "$ROOT_DIR/nettygame/pom.xml" -P integration-tests verify \
  -Dit.mysql.url="jdbc:mysql://127.0.0.1:3307/hero_story?useSSL=false&useUnicode=true&characterEncoding=UTF-8" \
  -Dit.mysql.username="hero" \
  -Dit.mysql.password="hero_pass" \
  -Dit.redis.host="127.0.0.1" \
  -Dit.redis.port="6380"
