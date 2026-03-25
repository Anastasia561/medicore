package pl.edu.medicore.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.validation.annotation.PDF;

public class PDFValidator implements ConstraintValidator<PDF, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) return false;

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return contentType != null && contentType.equals("application/pdf")
                || filename != null && filename.toLowerCase().endsWith(".pdf");
    }
}
