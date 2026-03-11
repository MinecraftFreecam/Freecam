from pathlib import Path

import pytest

from lint_changelog import read_version, MetadataError

FIXTURES = Path(__file__).parent / "test_fixtures"


@pytest.mark.parametrize(
    "metadata_file",
    [
        "missing_file.toml",
        "missing_version.toml",
        "missing_mod_table.toml",
        "empty_version.toml",
        "non_str_version.toml",
    ],
)
def test_invalid_version_file(metadata_file):
    with pytest.raises(MetadataError):
        read_version(FIXTURES / metadata_file)


def test_valid_version_file():
    read_version(FIXTURES / "valid_version.toml")
