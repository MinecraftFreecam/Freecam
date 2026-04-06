# Changelog build

Responsible for building and managing the [project changelog](../CHANGELOG.md).

## Usage

For convenience, you can call changelog tasks from the [root project](..) using `:changelog:<task>` path notation.
However, if you `cd` into this directory or specify it via `--project-dir`, then tasks may execute significantly faster.

```shell
# Slow
./gradlew :changelog:patchChangelog
```

```shell
# Fast
./gradlew --project-dir changelog patchChangelog
```

```shell
# Also fast
cd changelog
../gradlew patchChangelog
```

### Common tasks

- `getChangelog` to get the current version's changelog section.
- `getChangelog --unreleased` to get the in-progress 'unreleased' section.
- `getChangelog --project-version 1.3.6` to get `v1.3.6`'s changelog section.
- `getReleaseNotes` to get the current version's changelog, formatted as release notes (stripped of headers and links).
- `patchChangelog` updates the unreleased section to the given version. Typically run after bumping the version in `metadata.toml`.

## Upstream

We use the JetBrains [gradle-changelog-plugin]. Refer to the upstream README for more detailed documentation.

[gradle-changelog-plugin]: https://github.com/JetBrains/gradle-changelog-plugin
