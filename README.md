# ValorLegend

A lightweight multiplayer game backend built on Netty and Protobuf.

## Features

- WebSocket game server (`ServerMain`)
- Command-handler architecture with reflection-based registration
- Redis-backed ranking service
- Optional RocketMQ integration for async rank updates
- MyBatis + MySQL login persistence

## Project Layout

- `valorlegend/` - Java source, tests, and Maven build
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
mvn -f valorlegend/pom.xml clean verify
java -jar valorlegend/target/valorlegend-1.1.0.jar
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
| Server port | `valorlegend.server.port` | `VALOR_LEGEND_SERVER_PORT` | `12345` |
| Enable MySQL | `valorlegend.mysql.enabled` | `VALOR_LEGEND_MYSQL_ENABLED` | `true` |
| MySQL JDBC URL | `valorlegend.mysql.jdbc-url` | `VALOR_LEGEND_MYSQL_JDBC_URL` | local URL |
| MySQL user | `valorlegend.mysql.username` | `VALOR_LEGEND_MYSQL_USERNAME` | `root` |
| MySQL password | `valorlegend.mysql.password` | `VALOR_LEGEND_MYSQL_PASSWORD` | `ROOT` |
| Enable Redis | `valorlegend.redis.enabled` | `VALOR_LEGEND_REDIS_ENABLED` | `true` |
| Redis host | `valorlegend.redis.host` | `VALOR_LEGEND_REDIS_HOST` | `127.0.0.1` |
| Redis port | `valorlegend.redis.port` | `VALOR_LEGEND_REDIS_PORT` | `6379` |
| Enable RocketMQ | `valorlegend.rocketmq.enabled` | `VALOR_LEGEND_ROCKETMQ_ENABLED` | `true` |
| RocketMQ NameServer | `valorlegend.rocketmq.namesrv` | `VALOR_LEGEND_ROCKETMQ_NAMESRV` | `127.0.0.1:9876` |

Legacy `herostory.*` and `HERO_STORY_*` keys are still supported for backward compatibility.

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
