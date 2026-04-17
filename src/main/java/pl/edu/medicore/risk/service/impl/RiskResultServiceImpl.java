package pl.edu.medicore.risk.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.service.LabResultService;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.risk.mapper.RiskResultMapper;
import pl.edu.medicore.risk.model.Disease;
import pl.edu.medicore.risk.model.RiskGroup;
import pl.edu.medicore.risk.model.RiskResult;
import pl.edu.medicore.risk.repository.RiskResultRepository;
import pl.edu.medicore.risk.service.contract.RiskCalculatorService;
import pl.edu.medicore.risk.service.contract.RiskResultService;
import pl.edu.medicore.test.model.Test;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
class RiskResultServiceImpl implements RiskResultService {
    private final LabResultService labResultService;
    private final RiskCalculatorService riskCalculatorService;
    private final RiskResultRepository riskResultRepository;
    private final RiskResultMapper riskResultMapper;

    @Override
    @Transactional
    public void calculateRiskForTest(long testId) {
        List<LabResult> labResults = labResultService.getLabResultsByTestId(testId);
        if (labResults.isEmpty()) {
            throw new IllegalStateException("No lab results found for test");
        }
        save(labResults);
    }

    @Override
    public void calculateRiskForPatient(long patientId) {
        List<LabResult> labResults = labResultService.getLabResultsByPatientId(patientId);
        if (!labResults.isEmpty()) {
            save(labResults);
        }
    }

    @Override
    public List<RiskResultResponseDto> getLatestByPatientId(long patientId) {
        return riskResultRepository.getLatestByPatientId(patientId)
                .stream()
                .map(riskResultMapper::toDto)
                .toList();
    }

    private void save(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Test test = labResults.getFirst().getTest();

        for (Disease disease : Disease.values()) {
            RiskResult riskResult = new RiskResult();
            Double risk = calculateRisk(labResults, disease);
            riskResult.setRiskGroup(estimateRiskGroup(risk));
            riskResult.setPatient(patient);
            riskResult.setTest(test);
            riskResult.setRiskPercent(risk);
            riskResult.setDisease(disease);
            riskResultRepository.save(riskResult);
        }
    }

    private Double calculateRisk(List<LabResult> labResults, Disease disease) {
        return switch (disease) {
            case DIABETES -> calculateDiabetesRisk(labResults);
            case CKD -> calculateCKDRisk(labResults);
            case ANEMIA -> calculateAnemiaRisk(labResults);
        };
    }

    private Double calculateAnemiaRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        boolean pregnant = patient.isPregnant();

        Double hgb = extractParameterValue(labResults, Parameter.HGB);
        Double hct = extractParameterValue(labResults, Parameter.HCT);
        Double rbc = extractParameterValue(labResults, Parameter.RBC);

        return riskCalculatorService.calculateAnemiaRiskPercentage(hgb, hct, rbc, gender, pregnant);
    }

    private Double calculateDiabetesRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        double weight = patient.getWeight();
        double height = patient.getHeight();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        Double glucose = extractParameterValue(labResults, Parameter.GLUCOSE);

        return riskCalculatorService.calculateDiabetesRisk(weight, height, glucose, gender, age);
    }

    private Double calculateCKDRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        double weight = patient.getWeight();
        double height = patient.getHeight();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        Double creatinine = extractParameterValue(labResults, Parameter.CREATININE);

        return riskCalculatorService.calculateCKDRisk(creatinine, weight, height, gender, age);
    }

    private RiskGroup estimateRiskGroup(Double risk) {
        if (risk == null) return RiskGroup.UNKNOWN;

        if (risk <= 10) {
            return RiskGroup.NONE;
        } else if (risk <= 20) {
            return RiskGroup.LOW;
        } else if (risk <= 50) {
            return RiskGroup.MEDIUM;
        } else {
            return RiskGroup.HIGH;
        }
    }

    private Double extractParameterValue(List<LabResult> labResults, Parameter parameter) {
        return labResults.stream().filter(r -> r.getParameter() == parameter)
                .findFirst()
                .map(LabResult::getValue)
                .orElse(null);
    }
}
