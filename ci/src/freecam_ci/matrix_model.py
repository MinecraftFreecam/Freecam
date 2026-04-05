from dataclasses import dataclass, asdict, fields
from typing import List, Any


@dataclass
class MatrixUpload:
    """Upload configuration for a MatrixJob."""

    path: str
    name: str | None = None
    days: int = 90
    archive: bool = True

    def to_dict(self) -> dict:
        """Convert to dict, omitting None values."""
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def from_dict(cls, value: dict[str, Any]):
        """Parse from dict, validating input."""

        defaults = {field.name: field.default for field in fields(cls)}

        upload = cls(
            path=value.get("path"),  # type: ignore
            name=value.get("name"),  # type: ignore
            days=value.get("days", defaults["days"]),  # type: ignore
            archive=value.get("archive", defaults["archive"]),  # type: ignore
        )

        if upload.path is None:
            raise ValueError("MatrixUpload: missing required key path")

        if not isinstance(upload.path, str):
            raise ValueError("MatrixUpload: path must be a string")

        if not (isinstance(upload.days, int) and 0 < upload.days <= 90):
            raise ValueError("MatrixUpload: days must be an integer between 1 and 90")

        if not isinstance(upload.archive, bool):
            raise ValueError("MatrixUpload: archive must be a boolean")

        if upload.archive:
            if upload.name is None:
                raise ValueError(
                    "MatrixUpload: missing key name, expected when archive=True"
                )
            if not isinstance(upload.name, str):
                raise ValueError("MatrixUpload: name must be a string")
        else:
            if upload.name is not None:
                raise ValueError(
                    "MatrixUpload: unexpected key name, not expected when archive=False"
                )

        return upload


@dataclass
class MatrixJob:
    """A job in a GHA 'include' matrix."""

    name: str
    gradle_args: List[str]
    upload: MatrixUpload | None = None

    def to_dict(self) -> dict:
        """Convert to dict, omitting None values."""
        result = {
            k: v for k, v in asdict(self).items() if k != "upload" and v is not None
        }
        if self.upload:
            result["upload"] = self.upload.to_dict()
        return result

    @classmethod
    def from_dict(cls, value: dict[str, Any]):
        """Construct from a `build_matrix` TOML entry."""
        upload = None
        upload_dict = value.get("upload")
        if upload_dict is not None:
            if not upload_dict:
                raise ValueError("MatrixUpload cannot be empty")
            upload = MatrixUpload.from_dict(upload_dict)

        try:
            job = cls(
                name=value["name"],
                gradle_args=value["gradle_args"],
                upload=upload,
            )
        except KeyError as e:
            raise ValueError(f"MatrixJob missing required key {e.args[0]}") from None

        if not isinstance(job.name, str):
            raise ValueError("MatrixJob: name must be a string")

        if not isinstance(job.gradle_args, list):
            raise ValueError("MatrixJob: gradle_args must be a list")

        return job
