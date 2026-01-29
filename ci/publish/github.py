import subprocess
from pathlib import Path
from .publisher import Publisher
from .models import ReleaseMetadata, ProjectReleaseMetadata


class GithubPublisher(Publisher):
    def publish(self, release: ReleaseMetadata) -> None:
        tag = release.platforms.github.tag
        changelog = release.changelog

        entries = self._sorted_versions(release.versions)

        if self.verbose:
            print(f"[github] tag={tag} files={[e.filename for e in entries]}")

        if self.dry_run:
            print(f"[dry-run] gh release create {tag}")
            for e in entries:
                print(f"[dry-run] gh release upload {tag} {e.filename}")
            return

        # Check whether the release exists
        exists = (
            subprocess.call(
                ["gh", "release", "view", tag],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
            )
            == 0
        )

        if not exists:
            subprocess.check_call(
                [
                    "gh",
                    "release",
                    "create",
                    tag,
                    "--title",
                    release.display_name,
                    "--notes",
                    changelog,
                ]
            )

        for entry in entries:
            jar_path = self._resolve_jar(entry)
            subprocess.check_call(
                [
                    "gh",
                    "release",
                    "upload",
                    tag,
                    str(jar_path),
                    "--clobber",
                ]
            )

    def _resolve_jar(self, entry: ProjectReleaseMetadata) -> Path:
        jar = self.artifacts_dir / "builds" / entry.filename
        if not jar.exists():
            raise RuntimeError(f"referenced jar not found: {entry.filename}")
        return jar

    @staticmethod
    def _sorted_versions(
        versions: list[ProjectReleaseMetadata],
    ) -> list[ProjectReleaseMetadata]:
        def mc_key(v: ProjectReleaseMetadata):
            return tuple(int(p) for p in v.minecraft_version.split("."))

        return sorted(versions, key=lambda v: (mc_key(v), v.loader))
