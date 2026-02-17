# Contributing

Thanks for contributing.

## Development Setup

1. Install JDK 8+ and Maven 3.8+
2. Run:

```bash
mvn -f nettygame/pom.xml clean verify
```

## Pull Request Checklist

- Keep changes focused and minimal
- Add or update unit tests
- Ensure `mvn -f nettygame/pom.xml verify` passes
- Update documentation when behavior changes

## Commit Message Style

Use concise, imperative messages, for example:

- `fix: handle missing user in entry command`
- `test: add codec roundtrip coverage`

## Reporting Issues

Please include:

- expected behavior
- actual behavior
- reproduction steps
- logs or stack traces (if relevant)
