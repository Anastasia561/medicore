package pl.edu.medicore.test.service.contract;

import java.net.URL;

public interface UrlGeneratorService {
    URL generateViewUrl(Long testId);

    URL generateDownloadUrl(Long testId);
}
