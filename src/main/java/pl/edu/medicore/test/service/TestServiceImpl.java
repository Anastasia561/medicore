package pl.edu.medicore.test.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.infrastructure.messaging.event.FileUploadEvent;
import pl.edu.medicore.test.mapper.TestMapper;
import pl.edu.medicore.test.model.Test;
import pl.edu.medicore.test.repository.TestRepository;
import pl.edu.medicore.infrastructure.storage.StorageService;

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
    public long save(TestUploadRequestDto dto, Long patientId) {
        if (dto.file().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Optional<Test> latestTestOpt = testRepository.findTopByPatientIdOrderByDateDesc(patientId);

        boolean isNewest = latestTestOpt
                .map(latest -> dto.date().isAfter(latest.getDate()))
                .orElse(true);

        Test test = testMapper.toEntity(dto, patientService.getById(patientId));
        Test saved = testRepository.save(test);

        try {
            storageService.uploadFile(dto.file(), saved.getId());

            if (isNewest) {
                publisher.publishEvent(new FileUploadEvent(saved.getId()));
            }
            return saved.getId();

        } catch (Exception e) {
            storageService.deleteFile(saved.getId());
            throw new RuntimeException("Failed to save test", e);
        }
    }

    @Override
    public Test getById(Long testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));
    }
}
