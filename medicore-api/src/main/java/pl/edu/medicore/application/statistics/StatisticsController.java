package pl.edu.medicore.application.statistics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.application.statistics.dto.AdminStatisticsResponseDto;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

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
}
