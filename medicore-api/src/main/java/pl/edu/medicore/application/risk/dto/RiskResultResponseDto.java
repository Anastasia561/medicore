package pl.edu.medicore.application.risk.dto;

import pl.edu.medicore.application.risk.Disease;
import pl.edu.medicore.application.risk.RiskGroup;

import java.time.LocalDate;

public record RiskResultResponseDto(
        long patientId,
        Disease disease,
        RiskGroup riskGroup,
        double riskPercent,
        LocalDate testDate,
        LocalDate calculatedAt
) {
}
