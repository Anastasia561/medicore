package pl.edu.medicore.infrastructure.storage.contract;

import pl.edu.medicore.common.encryption.HashId;

import java.net.URL;

public interface UrlGeneratorService {
    URL generateViewUrl(HashId testId);

    URL generateDownloadUrl(HashId testId);
}
