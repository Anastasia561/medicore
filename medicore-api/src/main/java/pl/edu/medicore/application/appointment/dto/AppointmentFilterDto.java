package pl.edu.medicore.application.appointment.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.application.appointment.AppointmentStatus;
import pl.edu.medicore.application.doctor.Specialization;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

public record AppointmentFilterDto(
        @NotNull(message = "UserId is required")
        HashId userId,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate,
        AppointmentStatus status,
        Specialization specialization
) {
}
