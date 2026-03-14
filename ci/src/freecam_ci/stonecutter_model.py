from dataclasses import dataclass
from typing import Any


@dataclass
class ProjectEntry:
    """Structured representation of a stonecutter version-project entry."""

    project: str
    version: str | None = None
    buildscript: str | None = None

    @property
    def build_in_ci(self) -> bool:
        """Whether this project should be built by CI."""
        return self.project != "common"

    @classmethod
    def parse(cls, value: str | dict[str, Any]) -> "ProjectEntry":
        """Normalize a project entry from the stonecutter schema."""

        if isinstance(value, str):
            project, *extra = value.split(":", 2)
            return cls(
                project=project,
                version=extra[0] if extra else None,
                buildscript=extra[1] if len(extra) > 1 else None,
            )

        if isinstance(value, dict):
            try:
                return cls(
                    project=value["project"],
                    version=value.get("version"),
                    buildscript=value.get("buildscript"),
                )
            except KeyError as e:
                raise ValueError(f"Invalid project entry: {value!r}") from e

        raise ValueError(f"Unknown project entry type: {value!r}")
