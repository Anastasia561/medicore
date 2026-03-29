package pl.edu.medicore.labresult;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.service.PdfParserService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
public class LabResultController {
    private final PdfParserService pdfParserService;

    @PostMapping
    public void uploadPdf(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try (InputStream inputStream = file.getInputStream()) {
                String text = pdfParserService.extractText(inputStream);
                for (Parameter param : Parameter.values()) {
                    pdfParserService.parse(text, param);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
