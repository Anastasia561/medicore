package pl.edu.medicore.risk.dto;

import pl.edu.medicore.risk.model.Disease;
import pl.edu.medicore.risk.model.RiskGroup;

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
