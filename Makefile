.PHONY: help build test verify integration-test docker-up docker-up-full docker-down

help:
	@echo "Available targets:"
	@echo "  build             Build shaded jar"
	@echo "  test              Run unit tests"
	@echo "  verify            Run unit tests + coverage gate"
	@echo "  integration-test  Run Docker-backed MySQL/Redis integration tests"
	@echo "  docker-up         Start game-server + mysql + redis"
	@echo "  docker-up-full    Start full profile (includes RocketMQ + rank-worker)"
	@echo "  docker-down       Stop and remove compose resources"

build:
	mvn -B -f nettygame/pom.xml clean package

test:
	mvn -B -f nettygame/pom.xml test

verify:
	mvn -B -f nettygame/pom.xml clean verify

integration-test:
	./scripts/run-integration-tests.sh

docker-up:
	docker compose up --build

docker-up-full:
	docker compose --profile full up --build

docker-down:
	docker compose down -v
