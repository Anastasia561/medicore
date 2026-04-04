package pl.edu.medicore.infrastructure.storage;

import java.net.URL;

public interface UrlGeneratorService {
    URL generateViewUrl(Long testId);

    URL generateDownloadUrl(Long testId);
}
