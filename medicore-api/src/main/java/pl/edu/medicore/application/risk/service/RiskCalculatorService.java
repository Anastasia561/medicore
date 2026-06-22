package pl.edu.medicore.application.risk.service;

import pl.edu.medicore.application.patient.PregnancyStatus;
import pl.edu.medicore.application.person.Gender;

public interface RiskCalculatorService {
    Double calculateAnemiaRiskPercentage(Double hgb, Double hct, Double rbc, Gender gender, PregnancyStatus pregnancyStatus);

    Double calculateDiabetesRisk(Double weight, Double height, Double fpg, Gender gender, Integer age);

    Double calculateCKDRisk(Double scr, Double weight, Double height, Gender gender, Integer age);
}
