package pl.edu.medicore.application.risk.service;

import pl.edu.medicore.application.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

public interface RiskResultService {
    void calculateRiskForTest(HashId testId);

    void calculateRiskForPatient(HashId patientId);

    List<RiskResultResponseDto> getLatestByPatientId(HashId patientId);
}
