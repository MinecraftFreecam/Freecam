# CI Scripts

This directory contains scripts used in CI workflows.

## Local setup

[uv] is used to manage python dependencies.
Once installed, use `uv sync` to setup a project `.venv`.
You can run scripts via `uv run`, e.g. `uv run pytest` or `uv run ci/build_matrix.py`.

## IDE setup

- Install [uv].
- Install an Intellij python plugin:
  - [Python plugin][python-plugin]
  - [Python Community plugin][python-community-plugin]
- Under "Project Structure" → "SDKs" → "+" → "Add Python SDK from disk":
  - If you've already run `uv sync`, choose "select existing".
  - Otherwise, choose "generate new".
  - Select "Type: uv".
- Under "Project Structure" → "Modules" → "+" → "Import Module":
  - Select the `ci` directory
  - Select "create module from existing sources".
  - In the module's "dependencies" tab, set "Module SDK" to the uv SDK created earlier.

Intellij can run scripts and tests using its usual ▶ button.

## Managing dependencies

Use `uv add --project ci <dependency>` to add CI dependencies, also specify `--group dev` for dev-dependencies.
Some dev-dependencies may make more sense at the root project (i.e. without specifying `--project ci`).
Use `uv lock` to update the lockfile.

[python-community-plugin]: https://plugins.jetbrains.com/plugin/7322-python-community-edition
[python-plugin]: https://plugins.jetbrains.com/plugin/631-python
[uv]: https://docs.astral.sh/uv
