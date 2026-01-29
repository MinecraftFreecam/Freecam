import subprocess
from pathlib import Path
import pytest

from .github import GithubPublisher
from .models import (
    ReleaseMetadata,
    ProjectReleaseMetadata,
    Platforms,
)


@pytest.fixture
def artifacts(tmp_path: Path) -> Path:
    (tmp_path / "builds").mkdir()
    return tmp_path


def make_release(filename: str) -> ReleaseMetadata:
    return ReleaseMetadata(
        mod_version="1.3.6",
        display_name="Freecam 1.3.6",
        release_type="release",
        changelog="notes",
        platforms=Platforms(
            curseforge=Platforms.Curseforge(project_id="x"),
            modrinth=Platforms.Modrinth(project_id="y"),
            github=Platforms.Github(tag="v1.3.6"),
        ),
        versions=[
            ProjectReleaseMetadata(
                loader="fabric",
                minecraft_version="1.20.6",
                filename=filename,
                game_versions=["1.20.6"],
                java_versions=["VERSION_21"],
                relationships=[],
            )
        ],
    )


def test_missing_jar_fails_fast(artifacts: Path):
    publisher = GithubPublisher(artifacts, dry_run=False)
    release = make_release("missing.jar")

    with pytest.raises(RuntimeError, match="referenced jar not found"):
        publisher.publish(release)


def test_dry_run_does_not_call_subprocess(monkeypatch, artifacts: Path, capsys):
    calls = []

    monkeypatch.setattr(subprocess, "call", lambda *a, **k: calls.append(("call", a)))
    monkeypatch.setattr(
        subprocess, "check_call", lambda *a, **k: calls.append(("check", a))
    )

    publisher = GithubPublisher(artifacts, dry_run=True, verbose=True)
    release = make_release("file.jar")

    publisher.publish(release)

    assert calls == []
    out = capsys.readouterr().out
    assert "gh release create v1.3.6" in out


def test_uploads_existing_jar(monkeypatch, artifacts: Path):
    jar = artifacts / "builds" / "file.jar"
    jar.write_text("dummy")

    calls = []

    monkeypatch.setattr(
        subprocess,
        "call",
        lambda *a, **k: 1,  # release does not exist
    )
    monkeypatch.setattr(
        subprocess,
        "check_call",
        lambda args: calls.append(args),
    )

    publisher = GithubPublisher(artifacts)
    release = make_release("file.jar")

    publisher.publish(release)

    assert [
        "gh",
        "release",
        "create",
        "v1.3.6",
        "--title",
        "Freecam 1.3.6",
        "--notes",
        "notes",
    ] in calls
    assert ["gh", "release", "upload", "v1.3.6", str(jar), "--clobber"] in calls
