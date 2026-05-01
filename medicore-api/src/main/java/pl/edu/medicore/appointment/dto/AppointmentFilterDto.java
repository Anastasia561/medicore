package pl.edu.medicore.appointment.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.doctor.model.Specialization;

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
