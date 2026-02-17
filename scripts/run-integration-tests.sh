#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
COMPOSE_PROJECT="${COMPOSE_PROJECT_NAME:-netty-game-it}"

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_cmd docker
require_cmd mvn

if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose v2 is required (docker compose ...)." >&2
  exit 1
fi

compose() {
  docker compose -p "$COMPOSE_PROJECT" -f "$COMPOSE_FILE" --profile integration "$@"
}

cleanup() {
  compose down -v || true
}

trap cleanup EXIT

compose up -d mysql-it redis-it

for i in {1..60}; do
  if compose exec -T mysql-it mysqladmin ping -h 127.0.0.1 -uroot -proot_pass --silent >/dev/null 2>&1; then
    break
  fi

  if [[ "$i" -eq 60 ]]; then
    echo "MySQL did not become ready in time" >&2
    exit 1
  fi

  sleep 2
done

for i in {1..30}; do
  if compose exec -T redis-it redis-cli ping 2>/dev/null | grep -q '^PONG$'; then
    break
  fi

  if [[ "$i" -eq 30 ]]; then
    echo "Redis did not become ready in time" >&2
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
