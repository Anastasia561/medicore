package pl.edu.medicore.test.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.event.FileUploadEvent;
import pl.edu.medicore.test.model.Test;
import pl.edu.medicore.test.repository.TestRepository;
import pl.edu.medicore.test.service.contract.StorageService;
import pl.edu.medicore.test.service.contract.TestService;

@Service
@RequiredArgsConstructor
class TestServiceImpl implements TestService {
    private final PatientService patientService;
    private final TestRepository testRepository;
    private final StorageService storageService;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public long save(TestUploadRequestDto dto, Long patientId) {
        if (dto.file().isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        Test test = new Test();
        test.setDate(dto.date());
        test.setPatient(patientService.getById(patientId));

        Test saved = testRepository.save(test);

        try {
            storageService.uploadTest(dto.file(), saved.getId());
            publisher.publishEvent(new FileUploadEvent(saved.getId()));
            return saved.getId();

        } catch (Exception e) {
            storageService.deleteTest(saved.getId());
            throw new RuntimeException("Failed to save test", e);
        }
    }

    @Override
    public Test getById(Long testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));
    }
}
