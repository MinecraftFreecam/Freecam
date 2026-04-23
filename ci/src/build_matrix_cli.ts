import { z } from "zod";
import { existsSync } from "node:fs";
import { buildApplication, buildCommand } from "@stricli/core";
import path from "node:path";
import {
  MATRIX_JOBS_FILE,
  METADATA_FILE,
  STONECUTTER_FILE,
} from "./project_files.ts";
import { readVersion } from "./read_version.ts";
import { main } from "./build_matrix.ts";

const Version = z.string().min(1);

const ExistingFile = z.string().refine(existsSync, {
  message: "File does not exist",
});

const JobsFileInput = z.union([z.literal("none"), ExistingFile]);

function toRelative(p: string): string {
  return path.relative(process.cwd(), p);
}

const VersionInput = Version.optional().transform(
  (v?: string | undefined) => v ?? readVersion(METADATA_FILE),
);

const CliOptionsSchema = z.object({
  versionsFile: ExistingFile,
  jobsFile: JobsFileInput.transform((file: string) =>
    file === "none" ? undefined : file,
  ),
  release: z.boolean(),
  changelog: z.boolean(),
  version: VersionInput,
  output: z.string().optional(),
});

export type CliOptions = z.infer<typeof CliOptionsSchema>;

export const command = buildCommand({
  docs: {
    brief: "Build CI matrix for Freecam releases",
    fullDescription:
      "Generates GitHub Actions matrix jobs from stonecutter configuration and static job definitions.",
  },

  parameters: {
    flags: {
      versionsFile: {
        brief: "Path to stonecutter configuration file",
        placeholder: "file",
        kind: "parsed",
        parse: ExistingFile.parse,
        default: toRelative(STONECUTTER_FILE),
      },

      jobsFile: {
        brief: "Path to static matrix jobs file (or 'none' to disable)",
        placeholder: "file",
        kind: "parsed",
        parse: JobsFileInput.parse,
        default: toRelative(MATRIX_JOBS_FILE),
      },

      changelog: {
        brief:
          "Build changelog (current version if --release, otherwise unreleased)",
        kind: "boolean",
      },

      release: {
        brief: "Build in release mode",
        kind: "boolean",
      },

      version: {
        brief: `Project version (defaults to ${toRelative(METADATA_FILE)})`,
        placeholder: "version",
        kind: "parsed",
        parse: VersionInput.parse,
        optional: true,
      },

      output: {
        brief: "Write matrix JSON to file",
        placeholder: "file",
        kind: "parsed",
        parse: String,
        optional: true,
      },
    },
  },

  func: (flags: object) => main(CliOptionsSchema.parse(flags)),
});

export const app = buildApplication(command, {
  name: "build_matrix",
  scanner: {
    allowArgumentEscapeSequence: true,
    caseStyle: "allow-kebab-for-camel",
  },
  documentation: { caseStyle: "convert-camel-to-kebab" },
});

export default app;
