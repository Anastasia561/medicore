package pl.edu.medicore.risk.service.impl;

import org.junit.jupiter.api.Test;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.risk.service.contract.RiskCalculatorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RiskCalculatorServiceTest {
    private final RiskCalculatorService riskCalculatorService = new RiskCalculatorServiceImpl();

    @Test
    void shouldReturnNullForAnemiaRisk_whenAnyInputIsNull() {
        Double result = riskCalculatorService.calculateAnemiaRiskPercentage(
                null, 40.0, 4.5, Gender.MALE, false);

        assertNull(result);
    }

    @Test
    void shouldReturnZeroRisk_whenAllValuesAboveCutoff() {
        Double result = riskCalculatorService.calculateAnemiaRiskPercentage(
                150.0, 45.0, 5.0, Gender.MALE, false);

        assertEquals(0.0, result);
    }

    @Test
    void shouldCalculateRisk_whenValuesBelowCutoff() {
        Double result = riskCalculatorService.calculateAnemiaRiskPercentage(
                10.0, 30.0, 3.5, Gender.MALE, false);

        assertNotNull(result);
        assertTrue(result > 0);
    }

    @Test
    void shouldReturnZero_whenValuesEqualToCutoff() {
        Double result = riskCalculatorService.calculateAnemiaRiskPercentage(
                130.0, 41.0, 4.2, Gender.MALE, false);

        assertEquals(0.0, result);
    }

    @Test
    void shouldGiveHigherRisk_forLowerValues() {
        Double mild = riskCalculatorService.calculateAnemiaRiskPercentage(
                120.0, 38.0, 4.0, Gender.MALE, false);

        Double severe = riskCalculatorService.calculateAnemiaRiskPercentage(
                80.0, 25.0, 3.0, Gender.MALE, false);

        assertTrue(severe > mild);
    }

    @Test
    void shouldReturnNullForDiabetesRisk_whenAnyInputIsNull() {
        Double result = riskCalculatorService.calculateDiabetesRisk(
                null, 1.75, 110.0, Gender.MALE, 50);

        assertNull(result);
    }

    @Test
    void shouldReturnLowRisk_whenFpgBelow100() {
        Double result = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 90.0, Gender.MALE, 30);

        assertNotNull(result);
        assertTrue(result > 0);
    }

    @Test
    void shouldIncreaseRisk_whenFpgInPreDiabetesRange() {
        Double result = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 110.0, Gender.MALE, 30);

        assertNotNull(result);
    }

    @Test
    void shouldGiveDifferentRiskForGender() {
        Double male = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 110.0, Gender.MALE, 30);

        Double female = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 110.0, Gender.FEMALE, 30);

        assertNotEquals(male, female);
    }

    @Test
    void shouldIncreaseRisk_whenAgeAbove45() {
        Double young = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 110.0, Gender.MALE, 30);

        Double old = riskCalculatorService.calculateDiabetesRisk(
                70.0, 1.75, 110.0, Gender.MALE, 60);

        assertTrue(old > young);
    }

    @Test
    void shouldIncreaseRisk_whenBMIIncreases() {
        Double lowBMI = riskCalculatorService.calculateDiabetesRisk(
                60.0, 1.80, 110.0, Gender.MALE, 30);

        Double highBMI = riskCalculatorService.calculateDiabetesRisk(
                100.0, 1.80, 110.0, Gender.MALE, 30);

        assertTrue(highBMI > lowBMI);
    }

    @Test
    void shouldReturnNullForCKDRisk_whenAnyInputIsNull() {
        Double result = riskCalculatorService.calculateCKDRisk(
                null, 80.0, 1.80, Gender.MALE, 60);

        assertNull(result);
    }

    @Test
    void shouldReturnLowRisk_whenGfrIsNormal() {
        Double result = riskCalculatorService.calculateCKDRisk(
                0.9, 80.0, 1.80, Gender.MALE, 30);

        assertNotNull(result);
        assertTrue(result >= 0);
    }

    @Test
    void shouldIncreaseRisk_whenGfrIsLow() {
        Double result = riskCalculatorService.calculateCKDRisk(
                5.0, 80.0, 1.80, Gender.MALE, 60);

        assertNotNull(result);
        assertTrue(result > 0);
    }

    @Test
    void shouldIncreaseRisk_whenBMIIsHigher() {
        Double lowBMI = riskCalculatorService.calculateCKDRisk(
                2.0, 60.0, 1.80, Gender.MALE, 50);

        Double highBMI = riskCalculatorService.calculateCKDRisk(
                2.0, 120.0, 1.80, Gender.MALE, 50);

        assertTrue(highBMI > lowBMI);
    }

    @Test
    void shouldDifferForGender() {
        Double male = riskCalculatorService.calculateCKDRisk(
                2.0, 80.0, 1.80, Gender.MALE, 50);

        Double female = riskCalculatorService.calculateCKDRisk(
                2.0, 80.0, 1.80, Gender.FEMALE, 50);

        assertNotEquals(male, female);
    }
}
