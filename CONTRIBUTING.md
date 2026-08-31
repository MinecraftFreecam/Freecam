# Contributing

## New to Git or GitHub?

If you have never contributed to an open-source project before, here are some great resources to get you up to speed:
- [First Contributions Tutorial](https://github.com/firstcontributions/first-contributions) – A practical sandbox guide to making your first pull request.
- [GitHub's Quickstart Guide](https://docs.github.com/en/get-started/start-your-journey/hello-world) – Learn the basics of branches, commits, and PRs.
- [Atlassian's Git Beginner Guide](https://www.atlassian.com/git/tutorials) – Clear visual tutorials on using Git command-line tools.

## Setup

Freecam is built using Gradle. We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea) to develop locally.

Before contributing, you will need your own **fork** of Freecam from which you can create pull requests.
[Create a fork](https://github.com/MinecraftFreecam/Freecam/fork) using GitHub, then **clone** your fork using Git.

If you're using an IDE like IntelliJ IDEA, open your local clone as a project in your IDE.

Import the project as a standard Gradle project. In IntelliJ, this is known as a "Gradle Sync". This may take some time on the initial run.

## Project structure

The project is structured into several sub-projects. Most of the mod itself lives in `:common`, and this is where most changes should be made.
`:fabric`, `:neoforge`, and `:forge` contain implementation details specific to those loaders; avoid making changes here unless necessary.

### Minecraft-agnostic code

Sometimes it is possible to write code that has no dependency on Minecraft itself. In these cases, consider introducing a dedicated `java-library` sub-project.
For example, `:config` contains most of the configuration system's implementation and its unit tests; it can be built without any Minecraft version(s).

### User-visible text

Translation strings are stored in `:i18n`, for example:
```toml
# i18n/src/main/en_US/config.toml
[freecam.config.controls]
label = "Key Bindings"
tooltip = "Freecam key bindings."
```

New text should be added to TOML files under `i18n/src/main/en_US` and displayed in-game using Minecraft's translatable `Component` API.

The TOML filename acts as an arbitrary "category"; you can add a new file if your key doesn't fit into any existing category.

Translation files consist of nested TOML tables containing string literals. From Minecraft, each translation key is a dot-separated path:

```toml
# i18n/src/main/en_US/example.toml
[foo.bar]
x.y.z = "hello, world"
```

```java
import net.minecraft.network.chat.Component;

void example() {
  Component.translatable("foo.bar.x.y.z");
  // ⇒ 'hello, world'
}
```

#### Translating to another language

You can help [translate Freecam on Crowdin](https://crowdin.com/project/freecam).
If you're unfamiliar with Crowdin, you can read their [getting started as a translator guide](https://support.crowdin.com/for-translators).

If you'd like to translate a language we don't currently target, please [open an issue](https://github.com/MinecraftFreecam/Freecam/issues/new) asking us to enable the language.

### Build logic

We also have a lot of code dedicated to actually _building_ the project itself. Implementation details typically live in `:build-logic` sub-projects,
while configuration and orchestration live in project buildscripts like `common/build.gradle.kts`.

### CI/CD

CI & CD is implemented using GitHub Actions workflows in `.github/workflows/`, with supporting scripts in `ci/` written in TypeScript.
Generally, we prefer to implement any non-trivial logic in `ci/`, because it is easier to maintain and can be automatically tested.

Working on CI files requires [Node.js](https://nodejs.org).
Run `npm ci` to set up dependencies and `npm test` to run the test suite.
See `ci/README.md` for more detail.

## Multi-version

Freecam uses [Stonecutter](https://stonecutter.kikugie.dev/) to target multiple Minecraft versions.
Fundamentally, this means we use Stonecutter's "preprocessor directive" syntax within Java comments, when code needs to vary based on Minecraft version.

Generally, try to choose the most readable syntax for the job in hand. Often that is a `//~` [local replacement](https://stonecutter.kikugie.dev/wiki/v2/reference/syntax/replacements#local-replacements) directive:
```java
// From common/src/main/java/net/xolt/freecam/util/FreeCamera.java
private ClientLevel getClientLevel() {
    //~ if >=1.20.6 'clientLevel' -> '(ClientLevel) level()'
    return (ClientLevel) level();
}
```

But Stonecutter has many other options, including:
- [global replacements](https://stonecutter.kikugie.dev/wiki/v2/reference/syntax/replacements#global-replacements) — defined in the relevant Gradle buildscript
- [swaps](https://stonecutter.kikugie.dev/wiki/v2/reference/syntax/swaps)
- [conditions](https://stonecutter.kikugie.dev/wiki/v2/reference/syntax/conditions) — the ordinary `if`-then-`else` branching conditions

These directives can also be [scoped](https://stonecutter.kikugie.dev/wiki/v2/reference/syntax/scopes) in various ways.

## Testing your changes

You can run or debug Freecam in an IDE, using the various "run client" Gradle tasks from version-specific Gradle sub-projects.
For example, to run Freecam on Fabric 26.2, you can run `./gradlew :fabric:26.2:runClient`.
In IntelliJ, you can also find this task in the Gradle tool window under:
```
freecam | freecam (root) | fabric | 26.2 | Tasks | fabric | runClient
```

You can refer to upstream documentation for [Fabric Loom](https://docs.fabricmc.net/develop/loom/) (used for `:fabric` sub-projects)
and [Mod Dev Gradle](https://docs.neoforged.net/toolchain/docs/plugins/mdg/) (used for `:forge` and `:neoforge` sub-projects).

### Automated tests

Some sub-projects have automated tests. Run `./gradlew test` to execute them, or `./gradlew check` for a more thorough set of checks.

If you've changed any files in `:build-logic`, you can run `./gradlew --project-dir build-logic check`.
If you've changed any files in `ci`, you should run `npm test` (see [above](#cicd)).

CI will also run these checks for pull requests.

## Documenting changes

### Commit conventions

We follow [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) for our commit messages and pull request titles (e.g., `feat: add new flight mode` or `fix: resolve crash on 1.20`).

Try to break changes into discrete logical units, and commit each separately.
Use brief but descriptive commit summaries, with more detail in the commit message body (if necessary).
This helps keep the commit history clear and obvious, which can help with debugging and understanding the project history.

### User-facing changes

Changes that could affect end-users should be documented in [change files](https://knope.tech/reference/concepts/change-file/) in the `.changeset` directory.
Use one change file per change: a single PR may contain multiple changes, and a single change may be iterated on over several PRs.

If you have `knope` installed, or use our `nix-shell`, you can run `knope document-change` to create a new change in the change set.
This will interactively prompt for a _summary_ and semantic _type_ (`major`, `minor`, `patch`, etc).

You can also create change files manually:
```markdown
---
default: minor
---

# Added an awesome new feature

```
The `default` front-matter defines how the change affects the "default package", i.e. Freecam.

The total change set determines the version number chosen for the next release, following [Semantic Versioning](https://knope.tech/reference/concepts/semantic-versioning/).
All changes in the change set will automatically be included in the release notes written to `CHANGELOG.md`.

## Release process

Typically only Freecam maintainers will make releases.

The current release process is:
1. Create a new branch from the latest `main`.
2. Run `knope bump-version`. This will prepare a new release (bump version, update changelog, commit changes).
   - Use `--override-version <version>` to manually specify a version.
   - Use `--prerelease-label <label>` to make a pre-release (`alpha`, `beta`, etc).
   - Use `--dry-run` to see what _would_ happen without actually making changes.
3. Open a pull request, e.g. by running `gh pr create`.
4. Merge the pull request, e.g. `gh pr merge --auto`.
5. Check that CI/CD successfully published to GitHub, CurseForge, and Modrinth.
