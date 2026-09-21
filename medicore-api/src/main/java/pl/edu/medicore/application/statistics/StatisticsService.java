package pl.edu.medicore.application.statistics;

import pl.edu.medicore.application.statistics.dto.AdminStatisticsResponseDto;

public interface StatisticsService {
    AdminStatisticsResponseDto getAdminStatistics();
}
