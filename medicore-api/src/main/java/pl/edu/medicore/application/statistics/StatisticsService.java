package pl.edu.medicore.application.statistics;

import pl.edu.medicore.application.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsResponseDto;
import pl.edu.medicore.common.encryption.HashId;

public interface StatisticsService {
    AdminStatisticsResponseDto getAdminStatistics();

    DoctorStatisticsResponseDto getDoctorStatistics(HashId doctorId);
}
