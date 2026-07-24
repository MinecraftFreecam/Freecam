import TOML from "smol-toml";
import { existsSync, readFileSync } from "node:fs";
import { METADATA_FILE } from "./project_files.ts";
import { MetadataSchema } from "./metadata_model.ts";

export class MetadataError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "MetadataError";
  }
}

export function readVersion(metadataFile: string = METADATA_FILE): string {
  if (!existsSync(metadataFile)) {
    throw new MetadataError(`${metadataFile} not found`);
  }

  const toml = TOML.parse(readFileSync(metadataFile, "utf-8"));
  const { success, data, error } = MetadataSchema.safeParse(toml);

  if (!success) {
    throw new MetadataError(
      `Invalid or missing \`mod.version\` in ${metadataFile}: ${error}`,
    );
  }

  return data.mod.version;
}
