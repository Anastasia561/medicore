package pl.edu.medicore.statistics.dto;

import java.util.List;

public record DoctorStatisticsResponseDto(
        long totalPatients,
        long consultationsToday,
        List<ConsultationStatisticsDto> monthlyConsultations
) {
}
