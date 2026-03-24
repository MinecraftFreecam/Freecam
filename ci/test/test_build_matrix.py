import pytest
from freecam_ci.build_matrix import (
    build_version_matrix,
    load_matrix_jobs,
    load_versions,
)
from pathlib import Path

FIXTURES = Path(__file__).parent / "fixtures"


def test_load_versions_valid():
    path = FIXTURES / "valid_versions.toml"
    versions = load_versions(versions_file=path)
    assert isinstance(versions, dict)
    # basic structural checks
    for key, value in versions.items():
        assert isinstance(key, str)
        assert value is None or isinstance(value, list)
        if isinstance(value, list):
            assert all(isinstance(v, (str, dict)) for v in value)


def test_load_versions_invalid():
    path = FIXTURES / "invalid_versions.toml"
    # Invalid because the "versions" key is missing or schema doesn't match
    with pytest.raises(Exception):
        load_versions(versions_file=path)


def test_build_version_matrix_basic():
    versions = {
        "1.21": ["foo", "bar", "neoforge"],
        "1.20": ["common", "fabric", "forge"],
    }
    version = "1.2.3"
    matrix = build_version_matrix(version, versions)
    assert len(matrix) == 2
    names = [job.name for job in matrix]
    assert "Build 1.20" in names
    assert "Build 1.21" in names

    job_121 = next(job for job in matrix if job.name == "Build 1.21")
    assert job_121.gradle_args == [":neoforge:1.21:buildAndCollect"]

    job_120 = next(job for job in matrix if job.name == "Build 1.20")
    assert job_120.gradle_args == [
        ":fabric:1.20:buildAndCollect",
        ":forge:1.20:buildAndCollect",
    ]
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
