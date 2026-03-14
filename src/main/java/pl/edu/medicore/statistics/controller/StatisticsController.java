package pl.edu.medicore.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.statistics.dto.DoctorStatisticsResponseDto;
import pl.edu.medicore.statistics.service.StatisticsService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseWrapper<AdminStatisticsResponseDto> getAdminStatistics() {
        return ResponseWrapper.ok(statisticsService.getAdminStatistics());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseWrapper<DoctorStatisticsResponseDto> getDoctorStatistics(@PathVariable Long id) {
        return ResponseWrapper.ok(statisticsService.getDoctorStatistics(id));
    }
}
