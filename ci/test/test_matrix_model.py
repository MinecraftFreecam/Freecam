import pytest

from freecam_ci.matrix_model import MatrixJob


def test_matrixjob_to_dict():
    job = MatrixJob(
        name="Build test",
        gradle_args=[":common:test"],
        upload_name=None,
        upload_path="build/libs/*.jar",
        upload_days=None,
    )
    assert job.to_dict() == {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_path": "build/libs/*.jar",
    }


def test_matrixjob_from_dict():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_path": "build/libs/*.jar",
        "upload_name": "artifact",
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build test",
        gradle_args=[":common:test"],
        upload_name="artifact",
        upload_path="build/libs/*.jar",
        upload_days=None,
    )


def test_matrixjob_from_dict_upload_pair_required():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload_path": "build/libs/*.jar",
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
