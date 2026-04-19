package pl.edu.medicore.infrastructure.parser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.Test;
import pl.edu.medicore.labresult.model.Parameter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfParserServiceTest {
    private final PdfParserService pdfParserService = new PdfParserServiceImpl();

    @Test
    void shouldParseValue_whenValidLineExists() {
        String text = """
                HAEMATOLOGY
                RBC 4.96 10^12/L
                """;

        Double result = pdfParserService.parse(text, Parameter.RBC);

        assertNotNull(result);
        assertEquals(4.96, result);
    }

    @Test
    void shouldReturnNull_whenParameterNotFound() {
        String text = """
                HAEMATOLOGY
                HGB 150 g/L
                """;

        Double result = pdfParserService.parse(text, Parameter.RBC);

        assertNull(result);
    }

    @Test
    void shouldReturnNull_whenParameterNotFoundInValidSection() {
        String text = """
                METABOLIC
                RBC 5.5 10^12/L
                BIOCHEMISTRY
                GLUCOSE 100 mg/dL
                """;

        Double result = pdfParserService.parse(text, Parameter.RBC);

        assertNull(result);
    }

    @Test
    void shouldExtractTextFromPdf_whenInputIsValid() throws Exception {
        String content = "Hello PDF";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(content);
        contentStream.endText();
        contentStream.close();

        document.save(out);
        document.close();

        InputStream input = new ByteArrayInputStream(out.toByteArray());

        String result = pdfParserService.extractText(input);

        assertNotNull(result);
        assertTrue(result.contains(content));
    }
}
