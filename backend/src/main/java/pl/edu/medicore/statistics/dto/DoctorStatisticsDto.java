package pl.edu.medicore.statistics.dto;

import pl.edu.medicore.doctor.model.Specialization;

public record DoctorStatisticsDto(
        Specialization specialization,
        long count
) {
}
