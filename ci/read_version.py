import tomllib as toml
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CHANGELOG_FILE = ROOT / "CHANGELOG.md"
METADATA_FILE = ROOT / "metadata.toml"


class MetadataError(Exception):
    """Raised when the metadata is invalid."""

    pass


def read_version(metadata_file: Path) -> str:
    """Reads the project version from metadata_file."""

    if not metadata_file.exists():
        raise MetadataError(f"{metadata_file.name} not found")

    data = toml.loads(metadata_file.read_text())

    try:
        mod = data["mod"]
    except KeyError:
        raise MetadataError(f"No `mod` table found in {metadata_file.name}")

    try:
        version = mod["version"]
    except KeyError:
        raise MetadataError(f"No `mod.version` entry found in {metadata_file.name}")

    if not version or not isinstance(version, str):
        raise MetadataError(f"Invalid `mod.version` found in {metadata_file.name}")

    return version
