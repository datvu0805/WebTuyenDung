# Contributing

Thank you for considering a contribution to Fake-bank.

## Project Boundary

Fake-bank is an independent payment provider simulator. Contributions must keep the project generic and must not bind it to a specific external system or runtime.

## Documentation Language

All repository documentation, API descriptions, issue templates, examples, and code comments must be written in English.

## Development Standards

- Keep API behavior aligned with `openapi/openapi.yaml`.
- Prefer deterministic behavior over random outcomes.
- Keep scenario behavior explicit and documented.
- Add or update tests for behavior changes.
- Keep public APIs backward compatible unless the change is documented as breaking.
- Avoid adding dependencies unless they solve a real project need.

## Branch Strategy

- Use short, descriptive feature branches.
- Keep pull requests scoped to one feature, fix, or documentation change.
- Rebase or merge from the default branch before requesting review.

## Commit Rules

Use conventional commit style:

- `chore: ...` for tooling, setup, and maintenance
- `docs: ...` for documentation
- `feat: ...` for user-visible functionality
- `fix: ...` for bug fixes
- `test: ...` for tests
- `refactor: ...` for behavior-preserving changes

## Local Development

See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md).
