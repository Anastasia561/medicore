package pl.edu.medicore.consultation.dto;

import pl.edu.medicore.consultation.model.Workday;

import java.time.LocalTime;

public record ConsultationDto(
        Workday day,
        LocalTime startTime,
        LocalTime endTime
) {
}
