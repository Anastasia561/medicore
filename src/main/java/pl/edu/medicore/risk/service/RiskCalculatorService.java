package pl.edu.medicore.risk.service;

import pl.edu.medicore.person.model.Gender;

public interface RiskCalculatorService {
    Double calculateAnemiaRiskPercentage(Double hgb, Double hct, Double rbc, Gender gender, Boolean pregnant);

    Double calculateDiabetesRisk(Double weight, Double height, Double fpg, Gender gender, Integer age);

    Double calculateCKDRisk(Double scr, Double weight, Double height, Gender gender, Integer age);
}
