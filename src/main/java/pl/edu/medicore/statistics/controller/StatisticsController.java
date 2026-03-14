package pl.edu.medicore.statistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Statistics", description = "Endpoints for managing statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @Operation(summary = "Get patient, doctor, appointments statistics for admin dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseWrapper<AdminStatisticsResponseDto> getAdminStatistics() {
        return ResponseWrapper.ok(statisticsService.getAdminStatistics());
    }

    @Operation(summary = "Get patient, appointments statistics for doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseWrapper<DoctorStatisticsResponseDto> getDoctorStatistics(@PathVariable Long id) {
        return ResponseWrapper.ok(statisticsService.getDoctorStatistics(id));
    }
}
