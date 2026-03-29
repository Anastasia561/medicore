package pl.edu.medicore.labresult.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.test.service.contract.StorageService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class LabResultService {
    private final StorageService  storageService;
    private final PdfParserService pdfParserService;

    public void processLabResults(Long testId){
//        InputStream file = storageService.getFile(testId);
//        String text = pdfParserService.extractText(file);
//        for(pa)
//        pdfParserService.parse(text);
    }

}
