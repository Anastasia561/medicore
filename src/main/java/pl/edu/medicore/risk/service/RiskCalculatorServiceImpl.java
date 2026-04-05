package pl.edu.medicore.risk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.person.model.Gender;

@Service
@RequiredArgsConstructor
public class RiskCalculatorServiceImpl implements RiskCalculatorService {

    @Override
    public Double calculateAnemiaRiskPercentage(Double hgb, Double hct, Double rbc, Gender gender, Boolean pregnant) {
        if (hgb == null || hct == null || rbc == null || gender == null) return null;

        double hbCutoff = getHgbCutoff(gender, pregnant);
        double hctCutoff = getHctCutoff(gender, pregnant);
        double rbcCutoff = getRbcCutoff(gender);

        double hbDeficit = Math.max(0, 1 - hgb / hbCutoff);
        double hctDeficit = Math.max(0, 1 - hct / hctCutoff);
        double rbcDeficit = Math.max(0, 1 - rbc / rbcCutoff);

        double totalRisk = hbDeficit * 0.6 + hctDeficit * 0.3 + rbcDeficit * 0.1;
        return convertToRiskPercent(totalRisk);
    }

    @Override
    public Double calculateDiabetesRisk(Double weight, Double height, Double fpg, Gender gender, Integer age) {
        if (weight == null || height == null || fpg == null || gender == null || age == null) return null;
        double bmi = weight / (height * height);

        double baseRisk;
        if (fpg < 100) baseRisk = 0.05;
        else if (fpg <= 125) baseRisk = 0.15;
        else baseRisk = 0.50;

        double sd = 5.9;
        double orPerSD = (gender == Gender.MALE) ? 1.16 : 1.09;
        double nSD = (bmi - 25) / sd;
        double bmiRelativeRisk = Math.pow(orPerSD, nSD);

        double ageFactor = (age > 45) ? 1.2 : 1.0;

        double totalRisk = baseRisk * bmiRelativeRisk * ageFactor;
        return convertToRiskPercent(totalRisk);
    }

    @Override
    public Double calculateCKDRisk(Double scr, Double weight, Double height, Gender gender, Integer age) {
        if (scr == null || height == null || gender == null || age == null) return null;

        double gfr = calculateGFR(scr, gender, age);
        double bmi = weight / (height * height);

        double baseRisk = ckdRiskFromGFR(gfr);

        double bmiMultiplier = bmiCKDRiskMultiplier(bmi, gender);

        double totalRisk = baseRisk * bmiMultiplier;
        totalRisk = Math.min(totalRisk, 1);

        return convertToRiskPercent(totalRisk);
    }

    private double calculateGFR(double scr, Gender gender, int age) {
        double gfr = 0;

        if (gender == Gender.FEMALE) {
            if (scr <= 0.7) gfr = 144 * Math.pow(scr / 0.7, -0.329) * Math.pow(0.993, age);
            else gfr = 144 * Math.pow(scr / 0.7, -1.209) * Math.pow(0.993, age);
        } else {
            if (scr <= 0.9) gfr = 141 * Math.pow(scr / 0.9, -0.411) * Math.pow(0.993, age);
            else gfr = 141 * Math.pow(scr / 0.9, -1.209) * Math.pow(0.993, age);
        }

        return gfr;
    }

    private double ckdRiskFromGFR(double gfr) {
        if (gfr >= 90) return 0.05;
        else if (gfr >= 60) return 0.1;
        else if (gfr >= 45) return 0.25;
        else if (gfr >= 30) return 0.5;
        else if (gfr >= 15) return 0.75;
        else return 0.9;
    }

    private double bmiCKDRiskMultiplier(double bmi, Gender gender) {
        if (bmi >= 35 && gender == Gender.FEMALE) return 4.0;
        if (bmi >= 30 && gender == Gender.MALE) return 3.0;
        if (bmi >= 25) return 3.0;
        return 1.0;
    }

    private double convertToRiskPercent(double totalRisk) {
        double riskPercentage = totalRisk * 100;
        return Math.round(riskPercentage * 100.0) / 100.0;
    }

    private double getHgbCutoff(Gender gender, Boolean pregnant) {
        if (gender == Gender.MALE) return 130;
        if (gender == Gender.FEMALE) {
            if (pregnant) return 110;
            return 120;
        }

        throw new IllegalArgumentException("Cannot determine HGB cutoff for given patient");
    }

    private double getHctCutoff(Gender gender, Boolean pregnant) {
        if (gender == Gender.MALE) return 41;
        if (gender == Gender.FEMALE) {
            if (pregnant) return 33;
            return 36;
        }

        throw new IllegalArgumentException("Cannot determine HCT cutoff for given patient");
    }

    private double getRbcCutoff(Gender gender) {
        if (gender == Gender.MALE) return 4.2;
        if (gender == Gender.FEMALE) return 3.7;

        throw new IllegalArgumentException("Cannot determine HGB cutoff for given patient");
    }
}
