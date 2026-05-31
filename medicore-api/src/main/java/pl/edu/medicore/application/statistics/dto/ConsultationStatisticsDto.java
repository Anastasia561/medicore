package pl.edu.medicore.application.statistics.dto;

import pl.edu.medicore.application.appointment.AppointmentStatus;

public record ConsultationStatisticsDto(
        int month,
        AppointmentStatus status,
        long count
) {
}
