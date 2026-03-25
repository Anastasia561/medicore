package pl.edu.medicore.test.service.contract;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void uploadTest(MultipartFile file, Long testId);

    void deleteTest(Long testId);
}
