package pl.edu.medicore.application.risk.dto;

import pl.edu.medicore.application.risk.Disease;
import pl.edu.medicore.application.risk.RiskGroup;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

public record RiskResultResponseDto(
        HashId patientId,
        Disease disease,
        RiskGroup riskGroup,
        double riskPercent,
        LocalDate testDate,
        LocalDate calculatedAt
) {
}
