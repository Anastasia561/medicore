package pl.edu.medicore.consultation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.edu.medicore.consultation.model.Workday;

import java.time.LocalTime;

public record ConsultationCreateDto(
        @NotNull(message = "Doctor id is required")
        @Positive(message = "Doctor id must be greater than 0")
        Long doctorId,
        @NotNull(message = "Day is required")
        Workday day,
        @NotNull(message = "Start time is required")
        LocalTime startTime,
        @NotNull(message = "End time is required")
        LocalTime endTime
) {
}
