package pl.edu.medicore.consultation.dto;

import pl.edu.medicore.consultation.model.Workday;

import java.time.LocalTime;
import java.util.UUID;

public record ConsultationDto(
        UUID publicId,
        Workday day,
        LocalTime startTime,
        LocalTime endTime
) {
}
