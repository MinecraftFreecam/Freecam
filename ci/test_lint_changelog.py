import pytest
from pathlib import Path

from lint_changelog import lint, read_version


FIXTURES = Path(__file__).parent / "test_fixtures"


@pytest.mark.parametrize(
    "changelog_file",
    [
        "missing_release.md",
        "unreleased_after_release.md",
        "missing_footer.md",
    ],
)
def test_invalid_changelog(changelog_file):
    with pytest.raises(SystemExit):
        lint(
            version="1.2.3",
            changelog_file=(FIXTURES / changelog_file),
        )


def test_valid_changelog():
    lint(
        version="1.2.3",
        changelog_file=(FIXTURES / "ok_current_version.md"),
    )


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
    with pytest.raises(SystemExit):
        read_version(FIXTURES / metadata_file)


def test_valid_version_file():
    read_version(FIXTURES / "valid_version.toml")
