package pl.edu.medicore.application.prescription.dto;

import java.time.LocalDate;

public record PrescriptionDto(
        String medicine,
        String dosage,
        LocalDate startDate,
        LocalDate endDate,
        String frequency
) {
}
