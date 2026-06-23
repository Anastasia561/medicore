package pl.edu.medicore.application.appointment.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.application.appointment.AppointmentStatus;

import java.time.LocalDate;

public record AppointmentFilterDto(
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        AppointmentStatus status
) {
}
