package pl.edu.medicore.consultation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ConsultationUpdateDto(
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        @NotNull(message = "End time is required")
        LocalTime endTime
) {
}
