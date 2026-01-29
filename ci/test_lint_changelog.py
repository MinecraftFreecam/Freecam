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


def test_invalid_version_file():
    with pytest.raises(SystemExit):
        read_version(FIXTURES / "invalid_version.properties")


def test_valid_version_file():
    read_version(FIXTURES / "valid_version.properties")
