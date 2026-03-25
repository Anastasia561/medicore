package pl.edu.medicore.test.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.medicore.validation.annotation.PDF;

import java.time.LocalDate;

public record TestUploadRequestDto(
        @PDF
        MultipartFile file,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Test date can not be in the future")
        LocalDate date
) {
}
