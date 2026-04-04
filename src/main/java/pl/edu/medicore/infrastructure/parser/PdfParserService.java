package pl.edu.medicore.infrastructure.parser;

import pl.edu.medicore.labresult.model.Parameter;

import java.io.InputStream;

public interface PdfParserService {
    String extractText(InputStream inputStream);

    double parse(String text, Parameter param);
}
