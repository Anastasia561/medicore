package pl.edu.medicore.risk.service.contract;

import pl.edu.medicore.risk.dto.RiskResultResponseDto;

import java.util.List;

public interface RiskResultService {
    void calculateRisk(long testId);

    List<RiskResultResponseDto> getLatestByPatientId(long patientId);
}
