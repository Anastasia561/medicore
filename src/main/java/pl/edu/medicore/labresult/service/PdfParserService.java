package pl.edu.medicore.labresult.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import pl.edu.medicore.labresult.model.Parameter;

import java.io.InputStream;

import java.util.List;

@Service
public class PdfParserService {

    public void parse(String text, Parameter param) {
        String[] lines = text.split("\\r?\\n");

        boolean inSection = false;

        for (String line : lines) {
            String upperLine = normalize(line);

            if (containsAny(upperLine, param.getConfig().getSections())) {
                inSection = true;
                continue;
            }

            if (inSection && isNewSection(upperLine)) {
                inSection = false;
            }

            if (inSection && containsAlias(upperLine, param.getConfig().getAliases())) {
                double value = extractFirstValidNumber(line);

                if (value > 0) {
                    System.out.println(param.name() + " - " + value);
                    return;
                }
            }
        }

        System.out.println(param.name() + " - 0.0");
    }

    private String normalize(String line) {
        return line.toUpperCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean containsAlias(String line, List<String> aliases) {
        for (String alias : aliases) {
            if (line.matches(".*\\b" + alias + "\\b.*")) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String line, List<String> keywords) {
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewSection(String line) {
        return line.contains("URINALYSIS") ||
                line.contains("BIOCHEMISTRY") ||
                line.contains("LIPID") ||
                line.contains("THYROID") ||
                line.contains("METABOLIC") ||
                line.contains("ENDOCRINOLOGY");
    }

    private double extractFirstValidNumber(String line) {
        line = line.replace("\u00A0", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] tokens = line.split(" ");

        for (String token : tokens) {

            String lower = token.toLowerCase();

            if (lower.contains("x10") || token.contains("^") || lower.contains("/")) {
                continue;
            }

            String[] subtokens = token.split("[^0-9.]");

            for (String sub : subtokens) {
                if (sub.isEmpty()) continue;

                if (sub.length() > 1 && sub.startsWith("0") && !sub.startsWith("0.")) {
                    continue;
                }

                try {
                    double num = Double.parseDouble(sub);
                    return num;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return 0.0;
    }

    public String extractText(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF", e);
        }
    }
}
