package pl.edu.medicore.infrastructure.storage.contract;

import java.net.URL;
import java.util.UUID;

public interface UrlGeneratorService {
    URL generateViewUrl(UUID storageKey);

    URL generateDownloadUrl(UUID storageKey);
}
