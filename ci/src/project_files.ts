import path from "node:path";

export const CI = path.resolve(import.meta.dirname, "..");
export const ROOT = path.resolve(CI, "..");
export const CHANGELOG_FILE = path.resolve(ROOT, "CHANGELOG.md");
export const METADATA_FILE = path.resolve(ROOT, "metadata.toml");
export const STONECUTTER_FILE = path.resolve(ROOT, "stonecutter.settings.toml");
export const MATRIX_JOBS_FILE = path.resolve(CI, "matrix_jobs.toml");
