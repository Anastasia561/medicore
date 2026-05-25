package pl.edu.medicore.application.appointment.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.application.appointment.Status;
import pl.edu.medicore.application.doctor.Specialization;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentFilterDto(
        @NotNull(message = "UserId is required")
        UUID userId,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate,
        Status status,
        Specialization specialization
) {
}
