package pl.edu.medicore.application.test;

import pl.edu.medicore.application.test.dto.TestUploadRequestDto;

import java.util.UUID;

public interface TestService {
    UUID save(TestUploadRequestDto dto, Long patientId);

    Test getById(Long testId);
}
