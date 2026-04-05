import pytest

from freecam_ci.matrix_model import MatrixJob, MatrixUpload


def test_matrixjob_to_dict():
    job = MatrixJob(
        name="Build thing",
        gradle_args=[":common:thing"],
        upload=MatrixUpload(
            name="Thing",
            path="build/libs/*.jar",
            days=90,
            archive=True,
        ),
    )
    assert job.to_dict() == {
        "name": "Build thing",
        "gradle_args": [":common:thing"],
        "upload": {
            "name": "Thing",
            "path": "build/libs/*.jar",
            "days": 90,
            "archive": True,
        },
    }


def test_matrixjob_from_dict():
    value = {
        "name": "Build thing",
        "gradle_args": [":common:thing"],
        "upload": {
            "path": "build/libs/*.jar",
            "name": "artifact",
        },
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build thing",
        gradle_args=[":common:thing"],
        upload=MatrixUpload(
            name="artifact",
            path="build/libs/*.jar",
            days=90,
            archive=True,
        ),
    )


def test_matrixjob_to_dict_no_archive():
    job = MatrixJob(
        name="Build file",
        gradle_args=[":common:file"],
        upload=MatrixUpload(
            name=None,
            path="build/libs/*.jar",
            days=90,
            archive=False,
        ),
    )
    assert job.to_dict() == {
        "name": "Build file",
        "gradle_args": [":common:file"],
        "upload": {
            "path": "build/libs/*.jar",
            "days": 90,
            "archive": False,
        },
    }


def test_matrixjob_from_dict_no_archive():
    value = {
        "name": "Build file",
        "gradle_args": [":common:file"],
        "upload": {
            "path": "build/libs/*.jar",
            "archive": False,
        },
    }
    job = MatrixJob.from_dict(value)
    assert job == MatrixJob(
        name="Build file",
        gradle_args=[":common:file"],
        upload=MatrixUpload(
            name=None,
            path="build/libs/*.jar",
            days=90,
            archive=False,
        ),
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
        upload=None,
    )


def test_matrixjob_from_dict_empty_upload():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload": {},
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_name_required():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload": {
            "path": "build/libs/*.jar",
        },
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_path_required():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload": {
            "name": "test",
        },
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)


def test_matrixjob_from_dict_upload_path_required_no_archive():
    value = {
        "name": "Build test",
        "gradle_args": [":common:test"],
        "upload": {
            "name": "build/libs/*.jar",
            "archive": False,
        },
    }
    with pytest.raises(ValueError):
        MatrixJob.from_dict(value)
