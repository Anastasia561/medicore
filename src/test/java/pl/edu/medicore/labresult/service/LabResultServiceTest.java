package pl.edu.medicore.labresult.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.infrastructure.messaging.event.LabResultsExtractedEvent;
import pl.edu.medicore.infrastructure.parser.PdfParserService;
import pl.edu.medicore.infrastructure.storage.StorageService;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.repository.LabResultRepository;
import pl.edu.medicore.test.service.TestService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LabResultServiceTest {
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
        Long testId = 1L;

        InputStream mockStream = new ByteArrayInputStream("pdf".getBytes());
        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();

        when(storageService.getFile(testId)).thenReturn(mockStream);
        when(pdfParserService.extractText(mockStream)).thenReturn("parsed text");
        when(testService.getById(testId)).thenReturn(test);


        for (Parameter param : Parameter.values()) {
            when(pdfParserService.parse("parsed text", param)).thenReturn(1.0);
        }

        labResultService.processLabResults(testId);

        verify(storageService).getFile(testId);
        verify(pdfParserService).extractText(mockStream);
        verify(labResultRepository, times(Parameter.values().length)).save(any(LabResult.class));
        verify(publisher).publishEvent(any(LabResultsExtractedEvent.class));
    }

    @Test
    void shouldReturnLabResultsByTestId_whenInputIsValid() {
        Long testId = 1L;
        List<LabResult> expected = List.of(new LabResult(), new LabResult());

        when(labResultRepository.getLabResultsByTestId(testId)).thenReturn(expected);

        List<LabResult> result = labResultService.getLabResultsByTestId(testId);

        assertEquals(expected, result);
        verify(labResultRepository).getLabResultsByTestId(testId);
    }

    @Test
    void shouldReturnLabResultsByPatientId_whenInputU() {
        Long patientId = 1L;
        List<LabResult> expected = List.of(new LabResult());

        when(labResultRepository.getLatestLabResultsByPatientId(patientId)).thenReturn(expected);

        List<LabResult> result = labResultService.getLabResultsByPatientId(patientId);

        assertEquals(expected, result);
        verify(labResultRepository).getLatestLabResultsByPatientId(patientId);
    }
}
