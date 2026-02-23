package pl.edu.medicore.prescription.dto;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PrescriptionCreateDto(
        @NotNull(message = "Record id is required")
        Long recordId,

        @NotNull(message = "Medicine name is required")
        @Size(min = 1, max = 60, message = "Medicine name must be 1-60 characters")
        String medicine,

        @NotNull(message = "Dosage is required")
        @Size(min = 1, max = 20, message = "Dosage must be 1-20 characters")
        String dosage,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date cannot be in the past")
        LocalDate startDate,

        @FutureOrPresent(message = "End date cannot be in the past")
        LocalDate endDate,

        @NotNull(message = "Frequency is required")
        @Size(min = 1, max = 50, message = "Frequency must be 1-50 characters")
        String frequency
) {
}
