import { readFileSync, writeFileSync } from "node:fs";
import TOML from "smol-toml";
import {
  type MatrixJob,
  MatrixJobSchema,
  MatrixJobsFileSchema,
} from "./matrix_model.ts";
import {
  SCProjectsByVersionSchema,
  SCProjectSlugSchema,
} from "./stonecutter_model.ts";
import { MATRIX_JOBS_FILE } from "./project_files.ts";
import { readVersion } from "./read_version.ts";
import app, { type CliOptions } from "./build_matrix_cli.ts";
import { run, type StricliProcess } from "@stricli/core";

export function main(args: CliOptions) {
  const version = args.version ?? readVersion();

  const config = TOML.parse(readFileSync(args.versionsFile, "utf8"));
  const versionJobs = buildVersionMatrix(
    version + (args.release ? "" : "-SNAPSHOT"),
    config,
  );

  const changelogJobs = args.changelog
    ? [buildChangelogJob(args.release, version)]
    : [];

  const staticJobs = args.jobsFile
    ? loadMatrixJobs("build", args.jobsFile)
    : [];

  const matrix = [...changelogJobs, ...staticJobs, ...versionJobs].sort(
    (a, b) => a.name.localeCompare(b.name),
  );

  if (args.release) {
    matrix.forEach(({ gradle_args }) => {
      gradle_args.push("-PisReleaseBuild=true");
    });
  }

  const output = JSON.stringify(matrix, null, 4);

  if (args.output) {
    writeFileSync(args.output, JSON.stringify(matrix, null, 0));
  }

  console.log(output);
}

export function buildVersionMatrix(
  version: string,
  config: Record<string, unknown>,
  versionsKey = "versions",
): MatrixJob[] {
  const loaders = ["fabric", "forge", "neoforge"];
  const matrix: MatrixJob[] = [];

  // Extract and validate directly where the context is used
  const versions = SCProjectsByVersionSchema.parse(config[versionsKey]);

  for (const [key, branches] of Object.entries(versions)) {
    if (!Array.isArray(branches)) continue;

    const entry = SCProjectSlugSchema.parse(key);

    const gradleArgs = loaders
      .filter((loader) => branches.includes(loader))
      .map((loader) => `:${loader}:${entry.project}:buildAndCollect`);

    // Create a filtered copy of the stonecutter configuration
    const jobConfig = {
      ...config,
      [versionsKey]: { [key]: branches },
    };

    matrix.push(
      MatrixJobSchema.parse({
        name: `MC ${entry.project}`,
        gradle_args: gradleArgs,
        stonecutter_config: JSON.stringify(jobConfig),
        upload: {
          name: `mc-${entry.project}`,
          path: `build/libs/${version}/*.jar`,
        },
      }),
    );
  }

  return matrix;
}

export function buildChangelogJob(
  release = false,
  version?: string,
  file = "changelog.md",
): MatrixJob {
  if (release && !version) {
    throw new Error("buildChangelogJob: version is required when release=true");
  }

  return {
    name: "Changelog",
    gradle_args: [
      "--project-dir=changelog",
      ":getChangelog",
      release ? `--project-version=${version}` : "--unreleased",
      `--output-file=build/${file}`,
    ],
    upload: { path: `changelog/build/${file}`, days: 90, archive: false },
  };
}

export function loadMatrixJobs(
  key = "build",
  file = MATRIX_JOBS_FILE,
): MatrixJob[] {
  const toml = readFileSync(file, "utf8");
  const jobs = TOML.parse(toml)[key] ?? [];
  return MatrixJobsFileSchema.parse(jobs);
}

if (import.meta.main) {
  await run(app, process.argv.slice(2), { process: process as StricliProcess });
}
