from abc import ABC, abstractmethod
from pathlib import Path
from .models import ReleaseMetadata


class Publisher(ABC):
    def __init__(
        self, artifacts_dir: Path, dry_run: bool = False, verbose: bool = False
    ):
        self.artifacts_dir = artifacts_dir
        self.dry_run = dry_run
        self.verbose = verbose

    @abstractmethod
    def publish(self, release: ReleaseMetadata) -> None:
        pass
