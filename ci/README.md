# CI Scripts

This directory contains scripts used in CI workflows.

## Local setup

[NodeJS][node] is used to manage the CI project.
Once installed, use `npm install` to sync project dependencies.
You can run scripts via `node src/script.ts`, or `npm run <script>` for scripts defined in `package.json`.

You can check your code using `npm test` and format using `npm -w ci run format`.

## Managing dependencies

Use `npm install --workspace ci <dependency>` to add CI dependencies, also specify `--save-dev` or `-D` for dev-dependencies.
Some dev-dependencies may make more sense at the root project (i.e. without specifying `--workspace ci`).

[node]: https://nodejs.org
