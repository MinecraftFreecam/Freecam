from .publisher import Publisher
from .models import ReleaseMetadata


class ModrinthPublisher(Publisher):
    def publish(self, release: ReleaseMetadata) -> None:
        if self.verbose:
            print("[modrinth] not implemented")
