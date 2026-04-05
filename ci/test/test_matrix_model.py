import pytest

from freecam_ci.matrix_model import MatrixJob


def test_matrixjob_to_dict():
    job = MatrixJob(
        name="Build thing",
        gradle_args=[":common:thing"],
        upload_name="Thing",
        upload_path="build/libs/*.jar",
        upload_days=90,
        upload_archive=True,
    )
    assert job.to_dict() == {
        "name": "Build thing",
        "gradle_args": [":common:thing"],
        "upload_name": "Thing",
        "upload_path": "build/libs/*.jar",
        "upload_days": 90,
        "upload_archive": True,
    }


def test_matrixjob_from_dict():
    value = {
        "name": "Build thing",
        "gradle_args": [":common:thing"],
        "upload_path": "build/libs/*.jar",
        "upload_name": "artifact",
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build thing",
        gradle_args=[":common:thing"],
        upload_name="artifact",
        upload_path="build/libs/*.jar",
        upload_days=90,
        upload_archive=True,
    )


def test_matrixjob_to_dict_no_archive():
    job = MatrixJob(
        name="Build file",
        gradle_args=[":common:file"],
        upload_name=None,
        upload_path="build/libs/*.jar",
        upload_days=90,
        upload_archive=False,
    )
    assert job.to_dict() == {
        "name": "Build file",
        "gradle_args": [":common:file"],
        "upload_path": "build/libs/*.jar",
        "upload_days": 90,
        "upload_archive": False,
    }


def test_matrixjob_from_dict_no_archive():
    value = {
        "name": "Build file",
        "gradle_args": [":common:file"],
        "upload_path": "build/libs/*.jar",
        "upload_archive": False,
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build file",
        gradle_args=[":common:file"],
        upload_name=None,
        upload_path="build/libs/*.jar",
        upload_days=90,
        upload_archive=False,
    )


def test_matrixjob_to_dict_no_upload():
    job = MatrixJob(
        name="Build test",
        gradle_args=[":common:test"],
    )
    assert job.to_dict() == {
        "name": "Build test",
        "gradle_args": [":common:test"],
    }


def test_matrixjob_from_dict_no_upload():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build test",
        gradle_args=[":common:test"],
        upload_name=None,
        upload_path=None,
        upload_days=None,
        upload_archive=None,
    )


def test_matrixjob_from_dict_upload_name_required():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_path": "build/libs/*.jar",
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_path_required():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_name": "build/libs/*.jar",
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_path_required_no_archive():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_name": "build/libs/*.jar",
        "upload_archive": False,
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_days_requires_upload():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_days": 5,
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)
