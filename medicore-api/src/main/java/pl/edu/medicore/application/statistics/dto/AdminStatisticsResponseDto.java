package pl.edu.medicore.application.statistics.dto;

import java.util.List;

public record AdminStatisticsResponseDto(
        long totalPatients,
        long totalDoctors,
        long consultationsToday,
        List<ConsultationStatisticsDto> monthlyConsultations,
        List<DoctorStatisticsDto> doctorsBySpecialization
) {
}
