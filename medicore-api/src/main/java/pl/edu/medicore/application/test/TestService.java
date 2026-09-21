package pl.edu.medicore.application.test;

import java.net.URL;
import java.util.List;

import pl.edu.medicore.application.test.dto.TestResponseDto;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashId;

public interface TestService {
    HashId save(TestUploadRequestDto dto, HashId patientId);

    List<TestResponseDto> getAllForPatient(HashId patientId);

    Test getById(HashId testId);

    void checkExistsById(HashId id);

    URL generateViewUrl(HashId id);

    URL generateDownloadUrl(HashId id);
}
