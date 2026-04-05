from dataclasses import dataclass, asdict
from typing import List, Any


@dataclass
class MatrixJob:
    """A job in a GHA 'include' matrix."""

    name: str
    gradle_args: List[str]
    upload_name: str | None = None
    upload_path: str | None = None
    upload_days: int | None = None
    upload_archive: bool | None = None

    def to_dict(self) -> dict:
        """Convert to dict, omitting None values."""
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_dict(cls, value: dict[str, Any]):
        upload_name = value.get("upload_name")
        upload_path = value.get("upload_path")
        upload_days = value.get("upload_days")
        upload_archive = value.get("upload_archive")
        should_upload = any(
            field is not None
            for field in [
                upload_name,
                upload_path,
                upload_days,
                upload_archive,
            ]
        )

        if should_upload:
            if upload_archive is None:
                upload_archive = True

            if upload_days is None:
                upload_days = 90

            if upload_path is None:
                raise ValueError("upload_path must be defined when uploading")

            if upload_archive and upload_name is None:
                raise ValueError(
                    "upload_name must be defined when uploading a zip archive"
                )

        try:
            job = cls(
                name=value["name"],
                gradle_args=value["gradle_args"],
                upload_name=upload_name,
                upload_path=upload_path,
                upload_days=upload_days,
                upload_archive=upload_archive,
            )
        except KeyError as e:
            raise ValueError(f"MatrixJob missing required key {e.args[0]}") from None

        if not isinstance(job.name, str):
            raise ValueError("name must be a string")

        if not isinstance(job.gradle_args, list):
            raise ValueError("gradle_args must be a list")

        return job
