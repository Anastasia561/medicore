package pl.edu.medicore.application.labresult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.LabResultsExtractedEvent;
import pl.edu.medicore.infrastructure.parser.PdfParserService;
import pl.edu.medicore.infrastructure.storage.contract.StorageService;
import pl.edu.medicore.application.test.TestService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabResultServiceTest {
    @Mock
    private PdfParserService pdfParserService;
    @Mock
    private StorageService storageService;
    @Mock
    private TestService testService;
    @Mock
    private LabResultRepository labResultRepository;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private LabResultServiceImpl labResultService;

    @Test
    void shouldProcessLabResults_whenInputIsValid() {
        long testId = 1L;
        HashId hashId = new HashId(testId);
        UUID storageKey = UUID.randomUUID();

        InputStream mockStream = new ByteArrayInputStream("pdf".getBytes());
        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        test.setId(testId);
        test.setStorageKey(storageKey);

        when(storageService.getFile(storageKey)).thenReturn(mockStream);
        when(pdfParserService.extractText(mockStream)).thenReturn("parsed text");
        when(testService.getById(hashId)).thenReturn(test);


        for (Parameter param : Parameter.values()) {
            when(pdfParserService.parse("parsed text", param)).thenReturn(1.0);
        }

        labResultService.processLabResults(hashId);

        verify(storageService).getFile(storageKey);
        verify(pdfParserService).extractText(mockStream);
        verify(labResultRepository, times(Parameter.values().length)).save(any(LabResult.class));
        verify(publisher).publishEvent(any(LabResultsExtractedEvent.class));
    }

    @Test
    void shouldReturnLabResultsByTestId_whenInputIsValid() {
        Long testId = 1L;
        HashId hashId = HashId.of(testId);

        List<LabResult> expected = List.of(new LabResult(), new LabResult());

        when(labResultRepository.getLabResultsByTestId(testId)).thenReturn(expected);

        List<LabResult> result = labResultService.getLabResultsByTestId(hashId);

        assertEquals(expected, result);
        verify(labResultRepository).getLabResultsByTestId(testId);
    }

    @Test
    void shouldReturnLabResultsByPatientId_whenInputU() {
        long patientId = 1L;
        HashId patientHash = HashId.of(patientId);
        List<LabResult> expected = List.of(new LabResult());

        when(labResultRepository.getLatestLabResultsByPatientId(patientId)).thenReturn(expected);

        List<LabResult> result = labResultService.getLabResultsByPatientId(patientHash);

        assertEquals(expected, result);
        verify(labResultRepository).getLatestLabResultsByPatientId(patientId);
    }
}
