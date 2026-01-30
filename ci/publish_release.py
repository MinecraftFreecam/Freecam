#!/usr/bin/env python3

from __future__ import annotations

import argparse
import os
from pathlib import Path
from typing import Iterable

from publish.github import GithubPublisher
from publish.curseforge import CurseforgePublisher
from publish.modrinth import ModrinthPublisher
from publish.load import load_release_metadata
from publish.models import ReleaseMetadata


PUBLISHERS: dict[str, type] = {
    "github": GithubPublisher,
    "curseforge": CurseforgePublisher,
    "modrinth": ModrinthPublisher,
}


def build_publishers(
    *, artifacts: Path, dry_run: bool, verbose: bool, only: Iterable[str] | None
):
    selected = list(only) if only is not None else PUBLISHERS.keys()

    unknown = set(selected) - PUBLISHERS.keys()
    if unknown:
        raise SystemExit(f"Unknown publisher(s): {', '.join(sorted(unknown))}")

    if not selected:
        raise SystemExit("--only must select at least one publisher")

    for name in selected:
        yield PUBLISHERS[name](
            artifacts_dir=artifacts,
            dry_run=dry_run,
            verbose=verbose,
        )


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser(prog="publish")

    parser.add_argument(
        "metadata",
        type=Path,
        help="Path to release metadata JSON",
    )
    parser.add_argument(
        "artifacts",
        type=Path,
        help="Artifacts directory (contains builds/)",
    )

    parser.add_argument(
        "--dry-run",
        action=argparse.BooleanOptionalAction,
        default=os.environ.get("CI") is not None,
        help="Print actions without publishing (default: enabled outside of CI)",
    )

    parser.add_argument(
        "--only",
        metavar="PUBLISHER",
        nargs="+",
        choices=sorted(PUBLISHERS),
        help=f"Publish only to specific target(s): {', '.join(PUBLISHERS)}",
    )

    parser.add_argument("--verbose", action="store_true")

    args = parser.parse_args(argv)

    metadata: ReleaseMetadata = load_release_metadata(args.metadata)

    publishers = build_publishers(
        artifacts=args.artifacts,
        dry_run=args.dry_run,
        verbose=args.verbose,
        only=args.only,
    )

    for publisher in publishers:
        publisher.publish(metadata)


if __name__ == "__main__":
    main()
