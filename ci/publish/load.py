import json
from pathlib import Path

from .models import ReleaseMetadata


def load_release_metadata(path: Path) -> ReleaseMetadata:
    try:
        data = json.loads(path.read_text())
    except Exception as e:
        raise SystemExit(f"Failed to read {path}: {e}")

    try:
        return ReleaseMetadata.model_validate(data)
    except Exception as e:
        raise SystemExit(f"Invalid release metadata schema: {e}")
