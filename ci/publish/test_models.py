import json
from pathlib import Path

import pytest

from .models import ReleaseMetadata


FIXTURES = Path(__file__).parent.parent / "test_fixtures"


def test_valid_release_metadata():
    data = json.loads((FIXTURES / "release_1.3.6.json").read_text())
    metadata = ReleaseMetadata.model_validate(data)

    assert metadata.mod_version == "1.3.6"
    assert len(metadata.versions) > 1


def test_invalid_empty_versions():
    data = json.loads((FIXTURES / "release_1.3.6.json").read_text())
    data["versions"] = []

    with pytest.raises(Exception):
        ReleaseMetadata.model_validate(data)
