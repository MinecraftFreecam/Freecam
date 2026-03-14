"""
Used by .github/workflows/build.yml (prepare) to generate the job matrix.
"""

import argparse
import json
import tomllib as toml
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Any, List

import json5

from .project_files import MATRIX_JOBS_FILE, STONECUTTER_FILE
from .read_version import read_version
from .stonecutter_model import ProjectEntry


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

    @classmethod
    def from_dict(cls, value: dict[str, Any]):
        try:
            job = cls(
                name=value["name"],
                gradle_args=value["gradle_args"],
                upload_name=value.get("upload_name"),
                upload_path=value.get("upload_path"),
                upload_days=value.get("upload_days"),
            )
        except KeyError as e:
            raise ValueError(f"MatrixJob missing required key {e.args[0]}") from None

        if not isinstance(job.name, str):
            raise ValueError("name must be a string")

        if not isinstance(job.gradle_args, list):
            raise ValueError("gradle_args must be a list")

        if (job.upload_name is None) ^ (job.upload_path is None):
            raise ValueError("upload_name and upload_path must be defined together")

        if job.upload_days is not None and job.upload_name is None:
            raise ValueError("upload_days requires upload_name and upload_path")

        return job


def build_version_matrix(
    version: str,
    versions: dict[str, Any],
) -> list[MatrixJob]:
    matrix: list[MatrixJob] = []

    for version_name, projects in versions.items():
        normalized: list[ProjectEntry] = [ProjectEntry.parse(item) for item in projects]

        gradle_args: list[str] = [
            f":{entry.project}:{version_name}:buildAndCollect"
            for entry in normalized
            if entry.build_in_ci
        ]

        matrix.append(
            MatrixJob(
                name=f"Build {version_name}",
                gradle_args=gradle_args,
                upload_name=f"freecam-{version}-{version_name}",
                upload_path=f"build/libs/{version}/*.jar",
            )
        )

    return matrix


def load_versions(
    key: str = "versions", versions_file: Path = STONECUTTER_FILE
) -> dict[str, Any]:
    with versions_file.open("rb") as file:
        return json5.load(file)[key]


def load_matrix_jobs(
    key: str = "build",
    matrix_jobs_file: Path = MATRIX_JOBS_FILE,
) -> list[MatrixJob]:
    with matrix_jobs_file.open("rb") as file:
        data = toml.load(file)
    return [MatrixJob.from_dict(job) for job in data.get(key, [])]


def optional_path(value) -> Path | None:
    """argparse type representing a Path or 'none'"""
    if isinstance(value, str) and value.lower() in ["none", "null"]:
        return None
    return Path(value)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--versions-file",
        type=Path,
        default=STONECUTTER_FILE,
        help="path to stonecutter config file",
    )
    parser.add_argument(
        "--jobs-file",
        type=optional_path,
        default=MATRIX_JOBS_FILE,
        help="path to static matrix jobs file (pass 'none' to disable)",
    )
    parser.add_argument(
        "--version", type=str, help="project version (default read from metadata.toml)"
    )
    parser.add_argument("--output", type=Path, help="write matrix JSON to file")
    args = parser.parse_args()

    if not args.version:
        args.version = read_version()

    return args


def main() -> None:
    args = parse_args()

    version_jobs = build_version_matrix(
        version=args.version,
        versions=load_versions(versions_file=args.versions_file),
    )

    static_jobs: list[MatrixJob] = []
    if args.jobs_file:
        static_jobs = load_matrix_jobs(matrix_jobs_file=args.jobs_file)

    # Convert all jobs to dicts for JSON output
    matrix = [
        job.to_dict()
        for job in sorted(version_jobs + static_jobs, key=lambda job: job.name)
    ]

    # Print compact to output
    if args.output:
        args.output.write_text(
            json.dumps(matrix, sort_keys=True, separators=(",", ":"))
        )

    # Pretty print to stdout
    print(json.dumps(matrix, sort_keys=True, indent=4))


if __name__ == "__main__":
    main()
