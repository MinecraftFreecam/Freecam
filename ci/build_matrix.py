#!/usr/bin/env python3
"""
Used by .github/workflows/build.yml (prepare) to generate the job matrix.
"""

import argparse
import json
import tomllib as toml
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Any, List

from read_version import read_version

ROOT = Path(__file__).resolve().parents[1]
METADATA_FILE = ROOT / "metadata.toml"
STONECUTTER_FILE = ROOT / "stonecutter.versions.toml"


@dataclass
class MatrixJob:
    """A job in a GHA 'include' matrix."""

    name: str
    gradle_args: List[str]
    upload_name: str | None = None
    upload_path: str | None = None
    upload_days: int | None = None

    def to_dict(self) -> dict:
        """Convert to dict, omitting None values."""
        return {k: v for k, v in asdict(self).items() if v is not None}


def build_matrix(
    version: str,
    data: dict[str, Any],
) -> list[MatrixJob]:
    matrix: list[MatrixJob] = []

    for mc, entry in data.items():
        projects = entry.get("projects", [])
        gradle_args = [f":{p}:{mc}:buildAndCollect" for p in projects if p != "common"]

        matrix.append(
            MatrixJob(
                name=f"Build {mc}",
                gradle_args=gradle_args,
                upload_name=f"freecam-{version}-{mc}",
                upload_path=f"build/libs/{version}/*.jar",
            )
        )

    # Extra jobs
    matrix.append(
        MatrixJob(
            name="Build logic tests",
            gradle_args=["--project-dir", "build-logic", ":check"],
        )
    )

    return matrix


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--versions-file",
        type=Path,
        default=STONECUTTER_FILE,
        help="path to stonecutter.versions.toml",
    )
    parser.add_argument(
        "--version", type=str, help="project version (default read from metadata.toml)"
    )
    parser.add_argument("--output", type=Path, help="write matrix JSON to file")
    args = parser.parse_args()

    if not args.version:
        args.version = read_version(METADATA_FILE)

    return args


def main() -> None:
    args = parse_args()

    matrix = build_matrix(
        version=args.version,
        data=toml.loads(args.versions_file.read_text()),
    )

    # Convert all jobs to dicts for JSON output
    matrix = [job.to_dict() for job in matrix]

    # Print compact to output
    if args.output:
        args.output.write_text(
            json.dumps(matrix, sort_keys=True, separators=(",", ":"))
        )

    # Pretty print to stdout
    print(json.dumps(matrix, sort_keys=True, indent=4))


if __name__ == "__main__":
    main()
