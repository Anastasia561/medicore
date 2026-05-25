package pl.edu.medicore.application.consultation.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.application.consultation.Workday;

import java.time.LocalTime;
import java.util.UUID;

public record ConsultationCreateDto(
        @NotNull(message = "Doctor id is required")
        UUID doctorId,
        @NotNull(message = "Day is required")
        Workday day,
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        @NotNull(message = "End time is required")
        LocalTime endTime
) {
}
