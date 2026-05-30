package pl.edu.medicore.infrastructure.storage.contract;

import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.common.encryption.HashId;

import java.io.InputStream;

public interface StorageService {
    void uploadFile(MultipartFile file, HashId testId);

    void deleteFile(HashId testId);

    InputStream getFile(HashId testId);
}
