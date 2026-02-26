package pl.edu.medicore.appointment.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.appointment.model.Status;

import java.time.LocalDate;

public record AppointmentFilterDto(
        @NotNull(message = "UserId is required")
        Long userId,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate,
        Status status,
        String specialization
) {
}
