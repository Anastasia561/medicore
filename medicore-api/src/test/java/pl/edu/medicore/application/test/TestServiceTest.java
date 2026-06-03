package pl.edu.medicore.application.test;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.FileUploadEvent;
import pl.edu.medicore.infrastructure.storage.contract.StorageService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.infrastructure.storage.contract.UrlGeneratorService;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    @Mock
    private UrlGeneratorService urlGeneratorService;
    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void shouldReturnTest_whenTestExists() {
        long testId = 1L;
        HashId testHashId = new HashId(testId);

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        test.setId(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        pl.edu.medicore.application.test.Test result = testService.getById(testHashId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(testRepository).findById(testId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenTestNotFound() {
        long testId = 1L;
        HashId testHashId = new HashId(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> testService.getById(testHashId)
        );

        assertEquals("Test not found", exception.getMessage());
        verify(testRepository).findById(testId);
    }

    @Test
    void shouldSaveTestAndPublishEvent_whenFirstTest() {
        long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        MultipartFile file = mock(MultipartFile.class);
        TestUploadRequestDto dto = new TestUploadRequestDto(file, LocalDate.now());

        when(file.isEmpty()).thenReturn(false);

        Patient patient = new Patient();
        when(patientService.getById(patientHash)).thenReturn(patient);

        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId))
                .thenReturn(Optional.empty());

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        pl.edu.medicore.application.test.Test saved = new pl.edu.medicore.application.test.Test();
        saved.setId(patientId);
        saved.setId(10L);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        testService.save(dto, patientHash);

        verify(storageService).uploadFile(any(), any());
        verify(publisher).publishEvent(any(FileUploadEvent.class));
    }

    @Test
    void shouldSaveTestAndPublishEvent_whenNewestTest() {
        Long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        MultipartFile file = mock(MultipartFile.class);
        TestUploadRequestDto dto = new TestUploadRequestDto(file, LocalDate.now());

        when(file.isEmpty()).thenReturn(false);

        Patient patient = new Patient();
        when(patientService.getById(patientHash)).thenReturn(patient);

        pl.edu.medicore.application.test.Test oldTest = new pl.edu.medicore.application.test.Test();
        oldTest.setDate(LocalDate.now().minusDays(1));

        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId))
                .thenReturn(Optional.of(oldTest));

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        pl.edu.medicore.application.test.Test saved = new pl.edu.medicore.application.test.Test();
        saved.setId(10L);
        saved.setId(patientId);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        testService.save(dto, patientHash);

        verify(storageService).uploadFile(any(), any());
        verify(publisher).publishEvent(any(FileUploadEvent.class));
    }

    @Test
    void shouldNotPublishEvent_whenNotNewestTest() {
        Long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        MultipartFile file = mock(MultipartFile.class);
        TestUploadRequestDto dto =
                new TestUploadRequestDto(file, LocalDate.of(2020, 1, 1));

        when(file.isEmpty()).thenReturn(false);

        Patient patient = new Patient();
        when(patientService.getById(patientHash)).thenReturn(patient);

        pl.edu.medicore.application.test.Test latest = new pl.edu.medicore.application.test.Test();
        latest.setDate(LocalDate.of(2025, 1, 1));

        when(testRepository.findTopByPatientIdOrderByDateDesc(patientId))
                .thenReturn(Optional.of(latest));

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        pl.edu.medicore.application.test.Test saved = new pl.edu.medicore.application.test.Test();
        saved.setId(10L);
        saved.setId(patientId);

        when(testMapper.toEntity(dto, patient)).thenReturn(test);
        when(testRepository.save(test)).thenReturn(saved);

        testService.save(dto, patientHash);

        verify(publisher, never()).publishEvent(any(FileUploadEvent.class));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFileIsEmpty() {
        TestUploadRequestDto dto = mock(TestUploadRequestDto.class);
        MultipartFile file = mock(MultipartFile.class);

        when(dto.file()).thenReturn(file);
        when(file.isEmpty()).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> testService.save(dto, HashId.of(1L)));
        assertEquals("File is empty", ex.getMessage());

        verifyNoInteractions(testRepository, storageService, publisher);
    }

    @Test
    void shouldNotThrowEntityNotFoundException_whenTestExists() {
        long testId = 1L;
        HashId testHash = HashId.of(testId);

        when(testRepository.existsById(testId)).thenReturn(true);

        assertDoesNotThrow(() -> testService.checkExistsById(testHash));
        verify(testRepository, times(1)).existsById(testId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenTestDoesNotExist() {
        long testId = 1L;
        HashId testHash = HashId.of(testId);

        when(testRepository.existsById(testId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            testService.checkExistsById(testHash);
        });

        assertEquals("Test not found", exception.getMessage());
        verify(testRepository, times(1)).existsById(testId);
    }

    @Test
    void shouldGenerateViewUrl_whenTestExists() throws MalformedURLException {
        long testId = 1L;
        HashId testHash = HashId.of(testId);
        UUID storageKey = UUID.fromString("11100000-0000-0000-0000-000000000000");

        URL expectedUrl = new URL("https://example.com/view/11100000-0000-0000-0000-000000000000");
        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        test.setId(testId);
        test.setStorageKey(storageKey);

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        when(urlGeneratorService.generateViewUrl(storageKey)).thenReturn(expectedUrl);

        URL actualUrl = testService.generateViewUrl(testHash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlGeneratorService, times(1)).generateViewUrl(storageKey);
    }

    @Test
    void shouldGenerateDownloadUrl_whenTestExists() throws MalformedURLException {
        long testId = 1L;
        HashId testHash = HashId.of(testId);
        UUID storageKey = UUID.randomUUID();

        URL expectedUrl = new URL("https://example.com/view/11100000-0000-0000-0000-000000000000");
        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        test.setId(testId);
        test.setStorageKey(storageKey);

        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        when(urlGeneratorService.generateDownloadUrl(storageKey)).thenReturn(expectedUrl);

        URL actualUrl = testService.generateDownloadUrl(testHash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlGeneratorService, times(1)).generateDownloadUrl(storageKey);
    }
}
