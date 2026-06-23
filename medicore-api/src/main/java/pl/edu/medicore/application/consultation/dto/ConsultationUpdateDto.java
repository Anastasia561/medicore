package pl.edu.medicore.application.consultation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ConsultationUpdateDto(
        @NotNull(message = "Start startTime is required")
        LocalTime startTime,

        @NotNull(message = "End startTime is required")
        LocalTime endTime
) {
}
