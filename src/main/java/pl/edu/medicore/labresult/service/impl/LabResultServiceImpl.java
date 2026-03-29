package pl.edu.medicore.labresult.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.repository.LabResultRepository;
import pl.edu.medicore.labresult.service.contract.LabResultService;
import pl.edu.medicore.labresult.service.contract.PdfParserService;
import pl.edu.medicore.test.service.contract.StorageService;
import pl.edu.medicore.test.service.contract.TestService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
class LabResultServiceImpl implements LabResultService {
    private final PdfParserService pdfParserService;
    private final StorageService storageService;
    private final TestService testService;
    private final LabResultRepository labResultRepository;

    public void processLabResults(Long testId) {
        InputStream file = storageService.getFile(testId);
        String text = pdfParserService.extractText(file);

        for (Parameter param : Parameter.values()) {
            LabResult labResult = new LabResult();
            labResult.setUnit(param.getConfig().getStandardUnit());
            labResult.setParameter(param);
            double value = pdfParserService.parse(text, param);
            labResult.setValue(value);
            labResult.setTest(testService.getById(testId));

            labResultRepository.save(labResult);
        }
    }
}
