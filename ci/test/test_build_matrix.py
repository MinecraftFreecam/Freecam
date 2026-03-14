from pathlib import Path

import pytest

from freecam_ci.build_matrix import (
    build_version_matrix,
    load_matrix_jobs,
    MatrixJob,
    load_versions,
)

FIXTURES = Path(__file__).parent / "fixtures"


def test_matrixjob_to_dict():
    job = MatrixJob(
        name="Build test",
        gradle_args=[":common:test"],
        upload_name=None,
        upload_path="build/libs/*.jar",
        upload_days=None,
    )
    d = job.to_dict()
    assert "name" in d
    assert "gradle_args" in d
    assert "upload_name" not in d
    assert "upload_path" in d
    assert "upload_days" not in d


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


def test_load_versions_valid():
    for fixture_name in ["valid_versions.json5", "edge_versions.json5"]:
        path = FIXTURES / fixture_name
        versions = load_versions(versions_file=path)
        assert isinstance(versions, dict)
        # basic structural checks
        for key, value in versions.items():
            assert isinstance(key, str)
            assert value is None or isinstance(value, list)
            if isinstance(value, list):
                assert all(isinstance(v, (str, dict)) for v in value)


def test_load_versions_invalid():
    path = FIXTURES / "invalid_versions.json5"
    # Invalid because the "versions" key is missing or schema doesn't match
    with pytest.raises(Exception):
        load_versions(versions_file=path)


def test_build_version_matrix_basic():
    versions = {
        "1.21": ["common", "network"],
        "1.20": ["common", "core", "ui"],
    }
    version = "1.2.3"
    matrix = build_version_matrix(version, versions)
    assert len(matrix) == 2
    names = [job.name for job in matrix]
    assert "Build 1.20" in names
    assert "Build 1.21" in names

    # Check gradle_args
    job_120 = next(job for job in matrix if job.name == "Build 1.20")
    assert ":core:1.20:buildAndCollect" in job_120.gradle_args
    assert ":ui:1.20:buildAndCollect" in job_120.gradle_args
    assert job_120.upload_name == "freecam-1.2.3-1.20"
    assert job_120.upload_path == "build/libs/1.2.3/*.jar"
    assert all("common" not in arg for arg in job_120.gradle_args)


def test_load_matrix_jobs_empty():
    matrix = load_matrix_jobs(matrix_jobs_file=FIXTURES / "empty_matrix_jobs.toml")
    assert matrix == []


def test_load_matrix_jobs():
    matrix = load_matrix_jobs(matrix_jobs_file=FIXTURES / "matrix_jobs.toml")
    assert len(matrix) == 1

    assert matrix[0].name == "test job 1"
    assert matrix[0].gradle_args == ["a", "b", "c"]
    assert matrix[0].upload_name is None
    assert matrix[0].upload_path is None
    assert matrix[0].upload_days is None


def test_load_matrix_jobs_invalid():
    with pytest.raises(ValueError):
        load_matrix_jobs(matrix_jobs_file=FIXTURES / "bad_matrix_jobs.toml")
