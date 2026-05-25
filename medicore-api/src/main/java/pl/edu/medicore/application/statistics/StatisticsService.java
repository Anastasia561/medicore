package pl.edu.medicore.application.statistics;

import pl.edu.medicore.application.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsResponseDto;

import java.util.UUID;

public interface StatisticsService {
    AdminStatisticsResponseDto getAdminStatistics();

    DoctorStatisticsResponseDto getDoctorStatistics(UUID doctorId);
}
