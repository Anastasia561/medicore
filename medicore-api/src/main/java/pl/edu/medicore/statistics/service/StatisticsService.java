package pl.edu.medicore.statistics.service;

import pl.edu.medicore.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsResponseDto;

public interface StatisticsService {
    AdminStatisticsResponseDto getAdminStatistics();

    DoctorStatisticsResponseDto getDoctorStatistics(long doctorId);
}
