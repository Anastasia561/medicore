package pl.edu.medicore.application.consultation.dto;

import pl.edu.medicore.application.consultation.Workday;

import java.time.LocalTime;
import java.util.UUID;

public record ConsultationDto(
        UUID publicId,
        Workday day,
        LocalTime startTime,
        LocalTime endTime
) {
}
