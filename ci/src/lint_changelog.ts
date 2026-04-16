import fs from "node:fs";
import path from "node:path";
import { CHANGELOG_FILE } from "./project_files.ts";
import { readVersion, MetadataError } from "./read_version.ts";

export class LintError extends Error {
  constructor(message: string) {
    super(message);
    this.name = "LintError";
  }
}

type ParseResult = {
  unreleasedLine: number | null;
  releases: Map<string, number>;
  footerLinks: Set<string>;
};

function parseChangelog(text: string): ParseResult {
  let unreleasedLine: number | null = null;
  const releases = new Map<string, number>();
  const footerLinks = new Set<string>();

  const headerRe = /^## \[(?<name>[^\]]+)]/;
  const footerRe = /^\[(?<name>[^\]]+)]:/;

  const lines = text.split("\n");

  lines.forEach((raw, idx) => {
    const line = raw.replace(/\r$/, ""); // mimic rstrip for CRLF detection
    const lineNo = idx + 1;

    const headerMatch = line.match(headerRe);
    if (headerMatch?.groups?.name) {
      const name = headerMatch.groups.name;
      if (name === "Unreleased") {
        unreleasedLine = lineNo;
      } else {
        releases.set(name, lineNo);
      }
      return;
    }

    const footerMatch = line.match(footerRe);
    if (footerMatch?.groups?.name) {
      footerLinks.add(footerMatch.groups.name);
    }
  });

  return { unreleasedLine, releases, footerLinks };
}

export function lint(version: string, changelogFile: string): void {
  if (!fs.existsSync(changelogFile)) {
    throw new LintError(`${path.basename(changelogFile)} not found`);
  }

  const text = fs.readFileSync(changelogFile, "utf8");

  if (text.includes("\r\n")) {
    throw new LintError(
      `${path.basename(
        changelogFile,
      )} contains CRLF line endings; please convert to LF`,
    );
  }

  const { unreleasedLine, releases, footerLinks } = parseChangelog(text);

  if (!releases.has(version)) {
    throw new LintError(
      `Changelog has no release section for version ${version} ` +
        "(did you forget to run patchChangelog?)",
    );
  }

  if (unreleasedLine === null) {
    throw new LintError("Changelog is missing an [Unreleased] section");
  }

  if (unreleasedLine > (releases.get(version) as number)) {
    throw new LintError(
      "[Unreleased] section must appear before the current release section",
    );
  }

  if (!footerLinks.has("Unreleased")) {
    throw new LintError("Missing footer link for [Unreleased]");
  }

  if (!footerLinks.has(version)) {
    throw new LintError(`Missing footer link for version ${version}`);
  }

  console.log(`Changelog OK for version ${version}`);
}

export function main(): void {
  try {
    const version = readVersion();
    lint(version, CHANGELOG_FILE);
  } catch (e) {
    if (e instanceof LintError) {
      console.error("Changelog lint failed:\n");
      console.error(`- ${e.message}`);
      process.exit(1);
    }

    if (e instanceof MetadataError) {
      console.error("Changelog lint failed to read metadata:\n");
      console.error(`- ${e.message}`);
      process.exit(1);
    }

    throw e;
  }
}

if (import.meta.main) main();
