package pl.edu.medicore.application.risk.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.labresult.LabResult;
import pl.edu.medicore.application.labresult.Parameter;
import pl.edu.medicore.application.labresult.LabResultService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.patient.PregnancyStatus;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.application.risk.RiskResultMapper;
import pl.edu.medicore.application.risk.Disease;
import pl.edu.medicore.application.risk.RiskGroup;
import pl.edu.medicore.application.risk.RiskResult;
import pl.edu.medicore.application.test.Test;
import pl.edu.medicore.application.test.TestService;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
    public void calculateRiskForTest(HashId testId) {
        testService.checkExistsById(testId);

        List<LabResult> labResults = labResultService.getLabResultsByTestId(testId);
        if (labResults.isEmpty()) {
            throw new IllegalStateException("No lab results found for test");
        }
        save(labResults);
    }

    @Override
    public void calculateRiskForPatient(HashId patientId) {
        patientService.checkExistsById(patientId);
        List<LabResult> labResults = labResultService.getLabResultsByPatientId(patientId);
        if (!labResults.isEmpty()) {
            save(labResults);
        }
    }

    @Override
    public List<RiskResultResponseDto> getLatestByPatientId(HashId patientId) {
        patientService.checkExistsById(patientId);
        return riskResultRepository.getLatestByPatientPublicId(patientId.value())
                .stream()
                .map(riskResultMapper::toDto)
                .toList();
    }


    private List<String> determineMissingFields(Patient patient, List<LabResult> labResults, Disease disease) {
        List<String> missing = new ArrayList<>();

        if (patient.getGender() == null || patient.getGender() == Gender.OTHER) missing.add("gender");
        if (patient.getPregnancyStatus() == PregnancyStatus.UNKNOWN) missing.add("pregnancyStatus");

        switch (disease) {
            case DIABETES -> {
                if (patient.getWeight() == null || patient.getWeight() <= 0) missing.add("weight");
                if (patient.getHeight() == null || patient.getHeight() <= 0) missing.add("height");
                if (extractParameterValue(labResults, Parameter.GLUCOSE) == null) missing.add("glucoseTestResult");
            }
            case CKD -> {
                if (patient.getWeight() == null || patient.getWeight() <= 0) missing.add("weight");
                if (patient.getHeight() == null || patient.getHeight() <= 0) missing.add("height");
                if (extractParameterValue(labResults, Parameter.CREATININE) == null)
                    missing.add("creatinineTestResult");
            }
            case ANEMIA -> {
                if (patient.getPregnancyStatus() == null || patient.getPregnancyStatus() == PregnancyStatus.UNKNOWN) {
                    missing.add("pregnancyStatus");
                }
                if (extractParameterValue(labResults, Parameter.HGB) == null) missing.add("hgbTestResult");
                if (extractParameterValue(labResults, Parameter.HCT) == null) missing.add("hctTestResult");
                if (extractParameterValue(labResults, Parameter.RBC) == null) missing.add("rbcTestResult");
            }
        }
        return missing;
    }

    private void save(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Test test = labResults.getFirst().getTest();

        for (Disease disease : Disease.values()) {
            RiskResult riskResult = new RiskResult();

            List<String> missing = determineMissingFields(patient, labResults, disease);

            if (missing.isEmpty()) {
                Double risk = calculateRisk(labResults, disease);
                riskResult.setRiskPercent(risk);
                riskResult.setRiskGroup(estimateRiskGroup(risk));
                riskResult.setMissingFields(null);
            } else {
                riskResult.setRiskPercent(null);
                riskResult.setRiskGroup(RiskGroup.UNKNOWN);
                riskResult.setMissingFields(String.join(",", missing));
            }

            riskResult.setPatient(patient);
            riskResult.setTest(test);
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
        PregnancyStatus pregnancyStatus = patient.getPregnancyStatus();


        Double hgb = extractParameterValue(labResults, Parameter.HGB);
        Double hct = extractParameterValue(labResults, Parameter.HCT);
        Double rbc = extractParameterValue(labResults, Parameter.RBC);

        return riskCalculatorService.calculateAnemiaRiskPercentage(hgb, hct, rbc, gender, pregnancyStatus);
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
