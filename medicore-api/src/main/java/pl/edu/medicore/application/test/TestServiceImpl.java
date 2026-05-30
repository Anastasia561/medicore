package pl.edu.medicore.application.test;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.FileUploadEvent;
import pl.edu.medicore.infrastructure.storage.contract.StorageService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class TestServiceImpl implements TestService {
    private final PatientService patientService;
    private final TestRepository testRepository;
    private final StorageService storageService;
    private final TestMapper testMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public HashId save(TestUploadRequestDto dto, HashId patientId) {
        if (dto.file().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Optional<Test> latestTestOpt = testRepository.findTopByPatientIdOrderByDateDesc(patientId.value());

        boolean isNewest = latestTestOpt
                .map(latest -> dto.date().isAfter(latest.getDate()))
                .orElse(true);

        Test test = testMapper.toEntity(dto, patientService.getById(patientId));
        Test saved = testRepository.save(test);
        HashId id = HashId.of(saved.getId());

        try {
            storageService.uploadFile(dto.file(), id);

            if (isNewest) {
                publisher.publishEvent(new FileUploadEvent(id));
            }
            return HashId.of(saved.getId());

        } catch (Exception e) {
            storageService.deleteFile(id);
            throw new RuntimeException("Failed to save test", e);
        }
    }

    @Override
    public Test getById(HashId testId) {
        return testRepository.findById(testId.value())
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));
    }

    @Override
    public void checkExistsById(HashId id) {
        if (!testRepository.existsById(id.value())) {
            throw new EntityNotFoundException("Test not found");
        }
    }
}
