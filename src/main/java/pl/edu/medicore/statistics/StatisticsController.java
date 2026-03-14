package pl.edu.medicore.statistics;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/admin")
    public ResponseWrapper<AdminStatisticsResponseDto> getAdminStatistics() {
        return ResponseWrapper.ok(statisticsService.getAdminStatistics());
    }

    @GetMapping("/doctor/{id}")
    public ResponseWrapper<DoctorStatisticsResponseDto> getAdminStatistics(@PathVariable Long id) {
        return ResponseWrapper.ok(statisticsService.getDoctorStatistics(id));
    }
}
