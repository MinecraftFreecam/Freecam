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

    def to_dict(self) -> dict:
        """Convert to dict, omitting None values."""
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_dict(cls, value: dict[str, Any]):
        try:
            job = cls(
                name=value["name"],
                gradle_args=value["gradle_args"],
                upload_name=value.get("upload_name"),
                upload_path=value.get("upload_path"),
                upload_days=value.get("upload_days"),
            )
        except KeyError as e:
            raise ValueError(f"MatrixJob missing required key {e.args[0]}") from None

        if not isinstance(job.name, str):
            raise ValueError("name must be a string")

        if not isinstance(job.gradle_args, list):
            raise ValueError("gradle_args must be a list")

        if (job.upload_name is None) ^ (job.upload_path is None):
            raise ValueError("upload_name and upload_path must be defined together")

        if job.upload_days is not None and job.upload_name is None:
            raise ValueError("upload_days requires upload_name and upload_path")

        return job
