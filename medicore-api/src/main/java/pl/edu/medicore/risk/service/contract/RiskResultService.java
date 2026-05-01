package pl.edu.medicore.risk.service.contract;

import pl.edu.medicore.risk.dto.RiskResultResponseDto;

import java.util.List;
import java.util.UUID;

public interface RiskResultService {
    void calculateRiskForTest(long testId);

    void calculateRiskForPatient(UUID patientId);

    List<RiskResultResponseDto> getLatestByPatientId(UUID patientId);
}
