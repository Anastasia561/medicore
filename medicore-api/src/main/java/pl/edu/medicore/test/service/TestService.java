package pl.edu.medicore.test.service;

import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.model.Test;

import java.util.UUID;

public interface TestService {
    UUID save(TestUploadRequestDto dto, Long patientId);

    Test getById(Long testId);
}
