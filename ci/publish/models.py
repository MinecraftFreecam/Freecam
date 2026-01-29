from __future__ import annotations

from enum import Enum
from typing import List

from pydantic import BaseModel, Field, field_validator


class RelationshipType(str, Enum):
    required = "required"
    optional = "optional"
    bundled = "bundled"


class Relationship(BaseModel):
    curseforge_slug: str
    modrinth_id: str
    type: RelationshipType


class Platforms(BaseModel):
    class Curseforge(BaseModel):
        project_id: str

    class Modrinth(BaseModel):
        project_id: str

    class Github(BaseModel):
        tag: str

    curseforge: Curseforge
    modrinth: Modrinth
    github: Github


class ProjectReleaseMetadata(BaseModel):
    loader: str
    minecraft_version: str = Field(alias="minecraft_version")
    filename: str
    game_versions: List[str]
    java_versions: List[str]
    relationships: List[Relationship]

    @field_validator("game_versions")
    @classmethod
    def game_versions_non_empty(cls, v):
        if not v:
            raise ValueError("game_versions must not be empty")
        return v


class ReleaseMetadata(BaseModel):
    mod_version: str
    display_name: str
    release_type: str
    changelog: str
    platforms: Platforms
    versions: List[ProjectReleaseMetadata]

    @field_validator("versions")
    @classmethod
    def versions_non_empty(cls, v):
        if not v:
            raise ValueError("versions must not be empty")
        return v
