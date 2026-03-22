"""
Used by .github/workflows/build.yml (prepare) to generate the job matrix.
"""

import argparse
import json
import tomllib as toml
from pathlib import Path
from typing import Any

from .matrix_model import MatrixJob
from .project_files import MATRIX_JOBS_FILE, STONECUTTER_FILE
from .read_version import read_version
from .stonecutter_model import ProjectEntry


def build_version_matrix(
    version: str,
    versions: dict[str, Any],
) -> list[MatrixJob]:
    matrix: list[MatrixJob] = []

    for key, branches in versions.items():
        entry = ProjectEntry.parse(key)

        gradle_args: list[str] = [
            f":{branch}:{entry.project}:buildAndCollect"
            for branch in branches
            if branch != "common"
        ]

        matrix.append(
            MatrixJob(
                name=f"Build {entry.project}",
                gradle_args=gradle_args,
                upload_name=f"freecam-{version}-{entry.project}",
                upload_path=f"build/libs/{version}/*.jar",
            )
        )

    return matrix


def load_versions(
    key: str = "versions",
    versions_file: Path = STONECUTTER_FILE,
) -> dict[str, Any]:
    with versions_file.open("rb") as file:
        return toml.load(file)[key]


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
