from pathlib import Path

import pytest

from lint_changelog import lint, LintError

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
    with pytest.raises(LintError):
        lint(
            version="1.2.3",
            changelog_file=(FIXTURES / changelog_file),
        )


def test_valid_changelog():
    lint(
        version="1.2.3",
        changelog_file=(FIXTURES / "ok_current_version.md"),
    )
