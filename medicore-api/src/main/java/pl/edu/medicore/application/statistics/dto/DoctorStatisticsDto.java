package pl.edu.medicore.application.statistics.dto;

import pl.edu.medicore.application.doctor.Specialization;

public record DoctorStatisticsDto(
        Specialization specialization,
        long count
) {
}
