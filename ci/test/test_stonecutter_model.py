import pytest

from freecam_ci.build_matrix import ProjectEntry


def test_parse_string_entries():
    entry = ProjectEntry.parse("core:1.20.1")
    assert entry.project == "core"
    assert entry.version == "1.20.1"
    assert entry.buildscript is None

    entry2 = ProjectEntry.parse("ui:1.20.2:custom_build")
    assert entry2.project == "ui"
    assert entry2.version == "1.20.2"
    assert entry2.buildscript == "custom_build"


def test_parse_dict_entries():
    value = {"project": "fabric", "version": "1.19.2"}
    entry = ProjectEntry.parse(value)
    assert entry.project == "fabric"
    assert entry.version == "1.19.2"
    assert entry.buildscript is None


def test_parse_invalid_entries():
    with pytest.raises(ValueError):
        # Incorrect type
        # noinspection PyTypeChecker
        ProjectEntry.parse(123)

    with pytest.raises(ValueError):
        ProjectEntry.parse({"version": "1.0"})  # missing 'project'


def test_build_in_ci_property():
    common_entry = ProjectEntry.parse("common:1.18")
    assert common_entry.build_in_ci is False

    other_entry = ProjectEntry.parse("core:1.20")
    assert other_entry.build_in_ci is True
