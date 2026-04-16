import TOML, { type TomlTable } from "smol-toml";
import { existsSync, readFileSync } from "node:fs";
import { METADATA_FILE } from "./project_files.ts";

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
  const toml = readFileSync(metadataFile, "utf-8");

  const mod = TOML.parse(toml).mod as TomlTable;
  if (!mod || typeof mod !== "object") {
    throw new MetadataError(`No \`mod\` table found in ${metadataFile}`);
  }

  const version = mod?.version;
  if (!version) {
    throw new MetadataError(`No \`mod.version\` in ${metadataFile}`);
  }
  if (typeof version !== "string") {
    throw new MetadataError(`Invalid \`mod.version\` in ${metadataFile}`);
  }

  return version;
}
