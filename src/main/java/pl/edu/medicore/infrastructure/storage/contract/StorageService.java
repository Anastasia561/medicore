package pl.edu.medicore.infrastructure.storage.contract;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    void uploadFile(MultipartFile file, Long testId);

    void deleteFile(Long testId);

    InputStream getFile(Long testId);
}
