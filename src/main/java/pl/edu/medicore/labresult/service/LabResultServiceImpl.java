package pl.edu.medicore.labresult.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.edu.medicore.infrastructure.messaging.event.LabResultsExtractedEvent;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.repository.LabResultRepository;
import pl.edu.medicore.infrastructure.parser.PdfParserService;
import pl.edu.medicore.infrastructure.storage.StorageService;
import pl.edu.medicore.test.service.TestService;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
class LabResultServiceImpl implements LabResultService {
    private final PdfParserService pdfParserService;
    private final StorageService storageService;
    private final TestService testService;
    private final LabResultRepository labResultRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public void processLabResults(Long testId) {
        InputStream file = storageService.getFile(testId);
        String text = pdfParserService.extractText(file);

        for (Parameter param : Parameter.values()) {
            LabResult labResult = new LabResult();
            labResult.setUnit(param.getConfig().getStandardUnit());
            labResult.setParameter(param);
            Double value = pdfParserService.parse(text, param);
            labResult.setValue(value);
            labResult.setTest(testService.getById(testId));

            labResultRepository.save(labResult);
        }
        publisher.publishEvent(new LabResultsExtractedEvent(testId));
    }

    @Override
    public List<LabResult> getLabResultsByTestId(Long testId) {
        return labResultRepository.getLabResultsByTestId(testId);
    }
}
