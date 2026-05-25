package pl.edu.medicore.application.risk.service;

import pl.edu.medicore.application.risk.dto.RiskResultResponseDto;

import java.util.List;
import java.util.UUID;

public interface RiskResultService {
    void calculateRiskForTest(long testId);

    void calculateRiskForPatient(UUID patientId);

    List<RiskResultResponseDto> getLatestByPatientId(UUID patientId);
}
