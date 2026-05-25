package pl.edu.medicore.infrastructure.parser;

import pl.edu.medicore.application.labresult.Parameter;

import java.io.InputStream;

public interface PdfParserService {
    String extractText(InputStream inputStream);

    Double parse(String text, Parameter param);
}
