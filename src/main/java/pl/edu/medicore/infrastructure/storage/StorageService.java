package pl.edu.medicore.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    void uploadTest(MultipartFile file, Long testId);

    void deleteTest(Long testId);

    InputStream getFile(Long testId);
}
