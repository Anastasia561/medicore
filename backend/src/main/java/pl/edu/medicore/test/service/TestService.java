package pl.edu.medicore.test.service;

import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.model.Test;

public interface TestService {
    long save(TestUploadRequestDto dto, Long patientId);

    Test getById(Long testId);
}
