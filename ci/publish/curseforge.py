from .publisher import Publisher
from .models import ReleaseMetadata


class CurseforgePublisher(Publisher):
    def publish(self, release: ReleaseMetadata) -> None:
        if self.verbose:
            print("[curseforge] not implemented")
