package pl.edu.medicore.application.record.dto;

import java.time.LocalDate;

public record RecordFilterDto(
        LocalDate startDate,
        LocalDate endDate,
        String email
) {
}
