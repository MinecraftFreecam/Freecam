#!/usr/bin/env python3
"""
Changelog lint: ensure CHANGELOG.md corresponds to the current project version.

Ensures:
- The changelog must contain a release section for the current version
- An [Unreleased] section must exist
- [Unreleased] must appear before the current release section
- Footer links must include entries for both [Unreleased] and the current version

This intentionally does NOT validate changelog contents or section structure.
"""

import re
import sys
from pathlib import Path

from read_version import MetadataError, read_version

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG_FILE = ROOT / "CHANGELOG.md"
METADATA_FILE = ROOT / "metadata.toml"


class LintError(Exception):
    """Raised when the changelog is invalid."""

    pass


def parse_changelog(text: str):
    """
    Extract only the structural elements we care about:
    - section headers with their line numbers
    - footer reference definitions
    """

    unreleased_line = None
    release_lines = {}  # version -> line number
    footer_links = set()

    header_re = re.compile(r"^## \[(?P<name>[^\]]+)]")
    footer_re = re.compile(r"^\[(?P<name>[^\]]+)]:")

    for idx, raw in enumerate(text.splitlines(), start=1):
        line = raw.rstrip()
        if m := header_re.match(line):
            name = m.group("name")
            if name == "Unreleased":
                unreleased_line = idx
            else:
                # Accept any version-looking string here; exact match later
                release_lines[name] = idx
            continue

        if m := footer_re.match(line):
            footer_links.add(m.group("name"))

    return unreleased_line, release_lines, footer_links


def lint(version: str, changelog_file: Path) -> None:
    if not changelog_file.exists():
        raise LintError(f"{changelog_file.name} not found")

    text = changelog_file.read_text()
    if "\r\n" in text:
        raise LintError(
            f"{changelog_file.name} contains CRLF line endings; please convert to LF"
        )

    unreleased_line, releases, footer_links = parse_changelog(text)

    # Must have a release section matching the current version
    if version not in releases:
        raise LintError(
            f"Changelog has no release section for version {version} "
            "(did you forget to run patchChangelog?)"
        )

    # Must have an Unreleased section
    if unreleased_line is None:
        raise LintError("Changelog is missing an [Unreleased] section")

    # Unreleased must appear before the current release
    if unreleased_line > releases[version]:
        raise LintError(
            "[Unreleased] section must appear before the current release section"
        )

    # Footer links must exist
    if "Unreleased" not in footer_links:
        raise LintError("Missing footer link for [Unreleased]")

    if version not in footer_links:
        raise LintError(f"Missing footer link for version {version}")

    # All good
    print(f"Changelog OK for version {version}")


def main() -> None:
    try:
        version = read_version(METADATA_FILE)
        lint(version, CHANGELOG_FILE)
    except LintError as e:
        print("Changelog lint failed:\n", file=sys.stderr)
        print(f"- {e}", file=sys.stderr)
        sys.exit(1)
    except MetadataError as e:
        print("Changelog lint failed to read metadata:\n", file=sys.stderr)
        print(f"- {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
