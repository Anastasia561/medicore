package pl.edu.medicore.test.service.contract;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    void uploadTest(MultipartFile file, Long testId);

    void deleteTest(Long testId);

    InputStream getFile(Long testId);
}
