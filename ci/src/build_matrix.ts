import { readFileSync, writeFileSync } from "node:fs";
import TOML from "smol-toml";
import {
  type MatrixJob,
  MatrixJobSchema,
  MatrixJobsFileSchema,
} from "./matrix_model.ts";
import {
  type SCProjectsByVersion,
  SCProjectsByVersionSchema,
  SCProjectSlugSchema,
} from "./stonecutter_model.ts";
import { readVersion } from "./read_version.ts";
import app, { type CliOptions } from "./build_matrix_cli.ts";
import { run, type StricliProcess } from "@stricli/core";

export function main(args: CliOptions) {
  const version = args.version ?? readVersion();
  const versionsToml = TOML.parse(readFileSync(args.versionsFile, "utf8"));
  const matrixJobsToml = args.jobsFile
    ? TOML.parse(readFileSync(args.jobsFile, "utf8"))
    : null;

  const versionJobs = buildVersionMatrix(
    version + (args.release ? "" : "-SNAPSHOT"),
    SCProjectsByVersionSchema.parse(versionsToml.versions),
  );

  const staticJobs = matrixJobsToml
    ? MatrixJobsFileSchema.parse(matrixJobsToml).builds
    : [];

  const matrix = [...staticJobs, ...versionJobs].sort((a, b) =>
    a.name.localeCompare(b.name),
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
  versions: SCProjectsByVersion,
): MatrixJob[] {
  const loaders = ["fabric", "forge", "neoforge"];
  const matrix: MatrixJob[] = [];

  for (const [key, branches] of Object.entries(versions)) {
    if (!Array.isArray(branches)) continue;

    const entry = SCProjectSlugSchema.parse(key);

    const gradleArgs = loaders
      .filter((loader) => branches.includes(loader))
      .map((loader) => `:${loader}:${entry.project}:buildAndCollect`);

    matrix.push(
      MatrixJobSchema.parse({
        name: `MC ${entry.project}`,
        gradle_args: gradleArgs,
        upload: {
          name: `mc-${entry.project}`,
          path: `build/libs/${version}/*.jar`,
        },
      }),
    );
  }

  return matrix;
}

if (import.meta.main) {
  await run(app, process.argv.slice(2), { process: process as StricliProcess });
}
