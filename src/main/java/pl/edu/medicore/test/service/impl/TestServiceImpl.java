package pl.edu.medicore.test.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.labresult.service.LabResultService;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
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
    private final LabResultService labResultService;

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
            labResultService.processLabResults(test.getId());
            return saved.getId();

        } catch (Exception e) {
            storageService.deleteTest(saved.getId());
            throw new RuntimeException("Failed to save test", e);
        }
    }

    @Override
    @Transactional
    public void delete(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        testRepository.delete(test);

        try {
            storageService.deleteTest(testId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete test", e);
        }
    }
}
