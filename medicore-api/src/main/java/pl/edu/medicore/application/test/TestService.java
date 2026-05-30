package pl.edu.medicore.application.test;

import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashId;

public interface TestService {
    HashId save(TestUploadRequestDto dto, HashId patientId);

    Test getById(HashId testId);
}
