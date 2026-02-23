package pl.edu.medicore.prescription.dto;

import java.time.LocalDate;

public record PrescriptionDto(
        String medicine,
        String dosage,
        LocalDate startDate,
        LocalDate endDate,
        String frequency
) {
}
