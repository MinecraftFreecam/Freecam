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
  unreleasedLine: boolean;
  releases: Map<string, number>;
  oldFormatReleases: Set<string>;
  footerLinks: Set<string>;
};

function parseChangelog(text: string): ParseResult {
  let unreleasedLine: boolean = false;
  const releases = new Map<string, number>();
  const oldFormatReleases = new Set<string>();
  const footerLinks = new Set<string>();

  const headerRe = /^## (?<name>[-.0-9a-zA-Z]+) \((?<date>\d{4}-\d\d-\d\d)\)$/;
  const oldHeaderRe = /^## \[(?<name>[^\]]+)]/;
  const unreleasedRe = /^## \[?unreleased\]?/i;
  const footerRe = /^\[(?<name>[^\]]+)]:/;

  const lines = text.split("\n");

  lines.forEach((raw, idx) => {
    const line = raw.replace(/\r$/, ""); // mimic rstrip for CRLF detection
    const lineNo = idx + 1;

    if (line.match(unreleasedRe)) {
      unreleasedLine = true;
    }

    const headerMatch = line.match(headerRe);
    if (headerMatch?.groups?.name) {
      releases.set(headerMatch.groups.name, lineNo);
    }

    const oldHeaderMatch = line.match(oldHeaderRe);
    if (oldHeaderMatch?.groups?.name) {
      oldFormatReleases.add(oldHeaderMatch.groups.name);
    }

    const footerMatch = line.match(footerRe);
    if (footerMatch?.groups?.name) {
      footerLinks.add(footerMatch.groups.name);
    }
  });

  return { unreleasedLine, releases, oldFormatReleases, footerLinks };
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

  const { unreleasedLine, releases, oldFormatReleases, footerLinks } =
    parseChangelog(text);

  if (!releases.has(version)) {
    throw new LintError(
      `Changelog has no release section for version ${version} ` +
        "(did you forget to run patchChangelog?)",
    );
  }

  if (unreleasedLine) {
    throw new LintError("Unexpected 'Unreleased' section");
  }

  if (oldFormatReleases.size > 0) {
    throw new LintError(
      `Unexpected releases using the old format (${oldFormatReleases.size})`,
    );
  }

  if (footerLinks.size > 0) {
    throw new LintError(`Unexpected footer links (${footerLinks.size})`);
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
