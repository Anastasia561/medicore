package pl.edu.medicore.application.statistics.dto;

import java.util.List;

public record DoctorStatisticsResponseDto(
        long totalPatients,
        long consultationsToday,
        List<ConsultationStatisticsDto> monthlyConsultations
) {
}
