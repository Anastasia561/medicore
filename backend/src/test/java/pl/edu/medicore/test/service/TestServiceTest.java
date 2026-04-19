package pl.edu.medicore.test.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.infrastructure.messaging.event.FileUploadEvent;
import pl.edu.medicore.infrastructure.storage.contract.StorageService;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.mapper.TestMapper;
import pl.edu.medicore.test.repository.TestRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceTest {
    @Mock
    private PatientService patientService;
    @Mock
    private TestRepository testRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private TestMapper testMapper;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void shouldReturnTest_whenTestExists() {
        Long testId = 1L;
        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        test.setId(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        pl.edu.medicore.test.model.Test result = testService.getById(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(testRepository).findById(testId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenTestNotFound() {
        Long testId = 1L;

        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> testService.getById(testId)
        );

        assertEquals("Test not found", exception.getMessage());
        verify(testRepository).findById(testId);
    }

    @Test
    void shouldSaveTestAndPublishEvent_whenFirstTest() {
        Long patientId = 1L;
        Long testId = 10L;

        MultipartFile file = mock(MultipartFile.class);
        TestUploadRequestDto dto = new TestUploadRequestDto(file, LocalDate.now());
        when(file.isEmpty()).thenReturn(false);

        Patient patient = new Patient();
        when(patientService.getById(patientId)).thenReturn(patient);

        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId)).thenReturn(Optional.empty());

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        pl.edu.medicore.test.model.Test saved = new pl.edu.medicore.test.model.Test();
        saved.setId(testId);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        long result = testService.save(dto, patientId);

        assertEquals(testId, result);
        verify(storageService).uploadFile(file, testId);
        verify(publisher).publishEvent(any(FileUploadEvent.class));
    }

    @Test
    void shouldSaveTestAndPublishEvent_whenNewestTest() {
        Long patientId = 1L;
        Long testId = 10L;

        MultipartFile file = mock(MultipartFile.class);
        TestUploadRequestDto dto = new TestUploadRequestDto(file, LocalDate.now());
        when(file.isEmpty()).thenReturn(false);

        Patient patient = new Patient();
        when(patientService.getById(patientId)).thenReturn(patient);

        pl.edu.medicore.test.model.Test oldTest = new pl.edu.medicore.test.model.Test();
        oldTest.setDate(LocalDate.now().minusDays(1));
        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId)).thenReturn(Optional.of(oldTest));

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        pl.edu.medicore.test.model.Test saved = new pl.edu.medicore.test.model.Test();
        saved.setId(testId);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        long result = testService.save(dto, patientId);

        assertEquals(testId, result);
        verify(storageService).uploadFile(file, testId);
        verify(publisher).publishEvent(any(FileUploadEvent.class));
    }

    @Test
    void shouldNotPublishEvent_whenNotNewestTest() {
        Long patientId = 1L;
        Long testId = 10L;

        TestUploadRequestDto dto = mock(TestUploadRequestDto.class);
        MultipartFile file = mock(MultipartFile.class);

        when(dto.file()).thenReturn(file);
        when(file.isEmpty()).thenReturn(false);
        when(dto.date()).thenReturn(LocalDate.of(2020, 1, 1));

        Patient patient = new Patient();
        when(patientService.getById(patientId)).thenReturn(patient);

        pl.edu.medicore.test.model.Test latest = new pl.edu.medicore.test.model.Test();
        latest.setDate(LocalDate.of(2025, 1, 1));

        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId))
                .thenReturn(Optional.of(latest));

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        pl.edu.medicore.test.model.Test saved = new pl.edu.medicore.test.model.Test();
        saved.setId(testId);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        testService.save(dto, patientId);

        verify(storageService).uploadFile(file, testId);
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFileIsEmpty() {
        TestUploadRequestDto dto = mock(TestUploadRequestDto.class);
        MultipartFile file = mock(MultipartFile.class);

        when(dto.file()).thenReturn(file);
        when(file.isEmpty()).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> testService.save(dto, 1L));
        assertEquals("File is empty", ex.getMessage());

        verifyNoInteractions(testRepository, storageService, publisher);
    }
}
