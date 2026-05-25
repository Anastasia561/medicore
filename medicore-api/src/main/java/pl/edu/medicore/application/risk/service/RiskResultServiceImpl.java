package pl.edu.medicore.application.risk.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.labresult.LabResult;
import pl.edu.medicore.application.labresult.Parameter;
import pl.edu.medicore.application.labresult.LabResultService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.application.risk.RiskResultMapper;
import pl.edu.medicore.application.risk.Disease;
import pl.edu.medicore.application.risk.RiskGroup;
import pl.edu.medicore.application.risk.RiskResult;
import pl.edu.medicore.application.test.Test;
import pl.edu.medicore.application.test.TestService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class RiskResultServiceImpl implements RiskResultService {
    private final LabResultService labResultService;
    private final RiskCalculatorService riskCalculatorService;
    private final RiskResultRepository riskResultRepository;
    private final RiskResultMapper riskResultMapper;
    private final PatientService patientService;
    private final TestService testService;

    @Override
    @Transactional
    public void calculateRiskForTest(long testId) {
        testService.getById(testId);

        List<LabResult> labResults = labResultService.getLabResultsByTestId(testId);
        if (labResults.isEmpty()) {
            throw new IllegalStateException("No lab results found for test");
        }
        save(labResults);
    }

    @Override
    public void calculateRiskForPatient(UUID patientId) {
        patientService.getByPublicId(patientId);

        List<LabResult> labResults = labResultService.getLabResultsByPatientId(patientId);
        if (!labResults.isEmpty()) {
            save(labResults);
        }
    }

    @Override
    public List<RiskResultResponseDto> getLatestByPatientId(UUID patientId) {
        patientService.getByPublicId(patientId);
        return riskResultRepository.getLatestByPatientPublicId(patientId)
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
