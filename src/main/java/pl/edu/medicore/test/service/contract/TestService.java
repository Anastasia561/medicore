package pl.edu.medicore.test.service.contract;

import pl.edu.medicore.test.dto.TestUploadRequestDto;

public interface TestService {
    long save(TestUploadRequestDto dto, Long patientId);

    void delete(Long testId);
}
