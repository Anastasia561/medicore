package pl.edu.medicore.application.consultation.dto;

import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalTime;

public record ConsultationDto(
        HashId id,
        Workday day,
        LocalTime startTime,
        LocalTime endTime
) {
}
