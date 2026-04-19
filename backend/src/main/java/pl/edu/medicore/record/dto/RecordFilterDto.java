package pl.edu.medicore.record.dto;

import java.time.LocalDate;

public record RecordFilterDto(
        LocalDate startDate,
        LocalDate endDate,
        String specialization,
        String email
) {
}
