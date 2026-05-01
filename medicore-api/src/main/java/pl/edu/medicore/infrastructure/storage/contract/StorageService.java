package pl.edu.medicore.infrastructure.storage.contract;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public interface StorageService {
    void uploadFile(MultipartFile file, UUID testId);

    void deleteFile(UUID testId);

    InputStream getFile(UUID testId);
}
