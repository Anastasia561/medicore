package pl.edu.medicore.statistics.dto;

import pl.edu.medicore.appointment.model.Status;

public record ConsultationStatisticsDto(
        int month,
        Status status,
        long count
) {
}
