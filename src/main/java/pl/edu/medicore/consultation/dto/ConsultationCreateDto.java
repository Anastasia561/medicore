package pl.edu.medicore.consultation.dto;

import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.consultation.model.Workday;

import java.time.LocalTime;

public record ConsultationCreateDto(
        @NotNull(message = "Doctor id is required")
        Long doctorId,
        @NotNull(message = "Day is required")
        Workday day,
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        @NotNull(message = "End time is required")
        LocalTime endTime
) {
}
