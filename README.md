# Netty Game

A lightweight multiplayer game backend built on Netty and Protobuf.

## Features

- WebSocket game server (`ServerMain`)
- Command-handler architecture with reflection-based registration
- Redis-backed ranking service
- Optional RocketMQ integration for async rank updates
- MyBatis + MySQL login persistence

## Project Layout

- `nettygame/` - Java source, tests, and Maven build
- `deploy/mysql/init/` - MySQL bootstrap schema
- `.github/workflows/ci.yml` - CI pipeline (`mvn verify`)

## Requirements

- JDK 8+
- Maven 3.8+
- Docker + Docker Compose v2 (for containerized deployment and integration tests)
- GNU Make (optional, for shortcut commands)

Optional (for full runtime features):

- MySQL
- Redis
- RocketMQ

## Quick Start (Local)

```bash
mvn -f nettygame/pom.xml clean verify
java -jar nettygame/target/herostory-1.1.0.jar
```

Or use Make shortcuts:

```bash
make verify
make docker-up
```

Default server port: `12345`

## Runtime Configuration

Configuration can be passed via JVM properties or environment variables.

| Purpose | JVM Property | Environment Variable | Default |
| --- | --- | --- | --- |
| Server port | `herostory.server.port` | `HERO_STORY_SERVER_PORT` | `12345` |
| Enable MySQL | `herostory.mysql.enabled` | `HERO_STORY_MYSQL_ENABLED` | `true` |
| MySQL JDBC URL | `herostory.mysql.jdbc-url` | `HERO_STORY_MYSQL_JDBC_URL` | local URL |
| MySQL user | `herostory.mysql.username` | `HERO_STORY_MYSQL_USERNAME` | `root` |
| MySQL password | `herostory.mysql.password` | `HERO_STORY_MYSQL_PASSWORD` | `ROOT` |
| Enable Redis | `herostory.redis.enabled` | `HERO_STORY_REDIS_ENABLED` | `true` |
| Redis host | `herostory.redis.host` | `HERO_STORY_REDIS_HOST` | `127.0.0.1` |
| Redis port | `herostory.redis.port` | `HERO_STORY_REDIS_PORT` | `6379` |
| Enable RocketMQ | `herostory.rocketmq.enabled` | `HERO_STORY_ROCKETMQ_ENABLED` | `true` |
| RocketMQ NameServer | `herostory.rocketmq.namesrv` | `HERO_STORY_ROCKETMQ_NAMESRV` | `127.0.0.1:9876` |

## Docker Deployment

```bash
docker compose up --build
```

This starts:

- `game-server`
- `mysql`
- `redis`

RocketMQ + ranking worker can be started with profile:

```bash
docker compose --profile full up --build
```

## Quality Gates

- Unit tests: JUnit 5
- Coverage: JaCoCo check in `verify`
- CI: GitHub Actions for unit tests and coverage on all pushes/PRs
- Integration CI: Docker-backed MySQL/Redis tests on pull requests and pushes to `main`

## Integration Tests (MySQL + Redis)

The repository provides a Docker integration profile and an executable test script.

```bash
./scripts/run-integration-tests.sh
```

This command will:

- start `mysql-it` and `redis-it` with Docker profile `integration`
- run Maven integration tests (`ExternalDependenciesIT`) via profile `integration-tests`
- clean up containers automatically

## Community and Support

- Contribution guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Code of conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- Security policy: [SECURITY.md](SECURITY.md)
- Support channels: [SUPPORT.md](SUPPORT.md)

## License

MIT - see [LICENSE](LICENSE).
