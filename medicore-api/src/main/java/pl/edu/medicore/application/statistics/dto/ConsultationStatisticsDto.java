package pl.edu.medicore.application.statistics.dto;

import pl.edu.medicore.application.appointment.Status;

public record ConsultationStatisticsDto(
        int month,
        Status status,
        long count
) {
}
