package pl.edu.medicore.consultation.dto;

import java.time.LocalTime;

public record ConsultationUpdateDto(
        LocalTime startTime,
        LocalTime endTime
) {
}
