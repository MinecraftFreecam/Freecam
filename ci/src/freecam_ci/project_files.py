from pathlib import Path

CI = Path(__file__).resolve().parents[2]
ROOT = CI.parent
CHANGELOG_FILE = ROOT / "CHANGELOG.md"
METADATA_FILE = ROOT / "metadata.toml"
STONECUTTER_FILE = ROOT / "stonecutter.json5"
MATRIX_JOBS_FILE = CI / "matrix_jobs.toml"
