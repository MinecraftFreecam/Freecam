import json
from pathlib import Path
from subprocess import run


DIR = Path(__file__).parent
FIXTURES = DIR / "test_fixtures"


def test_publish_dry_run(tmp_path):
    metadata = FIXTURES / "release_1.3.6.json"

    artifacts = tmp_path / "artifacts"
    artifacts.mkdir()

    data = json.loads(metadata.read_text())
    for v in data["versions"]:
        (artifacts / v["filename"]).write_text("dummy jar")

    result = run(
        [
            "python",
            DIR / "publish_release.py",
            metadata,
            artifacts,
            "--dry-run",
            "--verbose",
        ],
        capture_output=True,
        text=True,
        check=True,
    )

    assert "[github]" in result.stdout
    assert "dry-run" in result.stdout
