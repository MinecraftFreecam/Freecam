from build_matrix import build_matrix, MatrixJob


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


def test_build_matrix_basic():
    data = {
        "$schema": "123456",
        "versions": {
            "1.21": ["common", "network"],
            "1.20": ["common", "core", "ui"],
            "1.19": [
                {"project": "common", "version": "1.19.2"},
                {"project": "fabric", "version": "1.19.2"},
                {"project": "leather", "version": "1.19.2"},
            ],
            "1.18": [
                "common:1.18.9",
                "forge:1.18.9",
            ],
        },
    }
    version = "1.2.3"
    matrix = build_matrix(version, data)
    assert len(matrix) == 5
    names = [job.name for job in matrix]
    assert "Build 1.20" in names
    assert "Build 1.21" in names
    assert "Build logic tests" in names

    # Check gradle_args
    job_120 = next(job for job in matrix if job.name == "Build 1.20")
    assert ":core:1.20:buildAndCollect" in job_120.gradle_args
    assert ":ui:1.20:buildAndCollect" in job_120.gradle_args
    assert job_120.upload_name == "freecam-1.2.3-1.20"
    assert job_120.upload_path == "build/libs/1.2.3/*.jar"
    assert all("common" not in arg for arg in job_120.gradle_args)

    job_119 = next(job for job in matrix if job.name == "Build 1.19")
    assert ":fabric:1.19:buildAndCollect" in job_119.gradle_args
    assert ":leather:1.19:buildAndCollect" in job_119.gradle_args
    assert job_119.upload_name == "freecam-1.2.3-1.19"
    assert job_119.upload_path == "build/libs/1.2.3/*.jar"
    assert all("common" not in arg for arg in job_119.gradle_args)

    job_118 = next(job for job in matrix if job.name == "Build 1.18")
    assert ":forge:1.18:buildAndCollect" in job_118.gradle_args
    assert job_118.upload_name == "freecam-1.2.3-1.18"
    assert job_118.upload_path == "build/libs/1.2.3/*.jar"
    assert all("common" not in arg for arg in job_118.gradle_args)
