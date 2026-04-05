package pl.edu.medicore.risk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.service.LabResultService;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Gender;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskResultServiceImpl implements RiskResultService {
    private final LabResultService labResultService;
    private final RiskCalculatorService riskCalculatorService;

    @Override
    public void calculateRisk(long testId) {
        System.out.println("Calculating risk results for test " + testId);
        List<LabResult> labResults = labResultService.getLabResultsByTestId(testId);
        if (labResults.isEmpty()) {
            throw new IllegalStateException("No lab results found for test");
        }

        calculateAnemiaRisk(labResults);
        calculateDiabetesRisk(labResults);
        calculateCKDRisk(labResults);
    }

    private void calculateAnemiaRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        boolean pregnant = patient.isPregnant();

        Double hgb = extractParameterValue(labResults, Parameter.HGB);
        Double hct = extractParameterValue(labResults, Parameter.HCT);
        Double rbc = extractParameterValue(labResults, Parameter.RBC);

        Double risk = riskCalculatorService.calculateAnemiaRiskPercentage(hgb, hct, rbc, gender, pregnant);

        if (risk == null) {
            System.out.println("Can not estimate risk");
        } else {
            System.out.println("Anemia risk: " + risk);
        }
    }

    private void calculateDiabetesRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        double weight = patient.getWeight();
        double height = patient.getHeight();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        Double glucose = extractParameterValue(labResults, Parameter.GLUCOSE);

        Double risk = riskCalculatorService.calculateDiabetesRisk(weight, height, glucose, gender, age);

        if (risk == null) {
            System.out.println("Can not estimate risk");
        } else {
            System.out.println("Diabetes risk: " + risk);
        }
    }

    private void calculateCKDRisk(List<LabResult> labResults) {
        Patient patient = labResults.getFirst().getTest().getPatient();
        Gender gender = patient.getGender();
        double weight = patient.getWeight();
        double height = patient.getHeight();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        Double creatinine = extractParameterValue(labResults, Parameter.CREATININE);

        Double risk = riskCalculatorService.calculateCKDRisk(creatinine, weight, height, gender, age);

        if (risk == null) {
            System.out.println("Can not estimate risk");
        } else {
            System.out.println("Diabetes risk: " + risk);
        }
    }

    private Double extractParameterValue(List<LabResult> labResults, Parameter parameter) {
        return labResults.stream().filter(r -> r.getParameter() == parameter)
                .findFirst()
                .map(LabResult::getValue)
                .orElse(null);
    }
}
