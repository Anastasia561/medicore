package pl.edu.medicore.risk.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.labresult.model.LabResult;
import pl.edu.medicore.labresult.model.Parameter;
import pl.edu.medicore.labresult.service.LabResultService;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.risk.mapper.RiskResultMapper;
import pl.edu.medicore.risk.model.Disease;
import pl.edu.medicore.risk.model.RiskGroup;
import pl.edu.medicore.risk.model.RiskResult;
import pl.edu.medicore.risk.repository.RiskResultRepository;
import pl.edu.medicore.risk.service.contract.RiskCalculatorService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskResultServiceTest {
    @Mock
    private LabResultService labResultService;
    @Mock
    private RiskCalculatorService riskCalculatorService;
    @Mock
    private PatientService patientService;
    @Mock
    private RiskResultRepository riskResultRepository;
    @Mock
    private RiskResultMapper riskResultMapper;
    @InjectMocks
    private RiskResultServiceImpl riskService;

    @Test
    void shouldCalculate3RiskForTest_whenLabResultsExist() {
        long testId = 1L;
        Patient patient = new Patient();
        patient.setGender(Gender.MALE);
        patient.setPregnant(false);
        patient.setWeight(60);
        patient.setHeight(180);
        patient.setBirthDate(LocalDate.of(1990, 10, 10));

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        test.setPatient(patient);

        LabResult hgbResult = new LabResult();
        hgbResult.setTest(test);
        hgbResult.setParameter(Parameter.HGB);
        hgbResult.setValue(20.6);

        LabResult hctResult = new LabResult();
        hctResult.setTest(test);
        hctResult.setParameter(Parameter.HCT);
        hctResult.setValue(30.6);

        LabResult rbcResult = new LabResult();
        rbcResult.setTest(test);
        rbcResult.setParameter(Parameter.RBC);
        rbcResult.setValue(10.6);


        List<LabResult> labResults = List.of(hgbResult, hctResult, rbcResult);

        when(labResultService.getLabResultsByTestId(testId)).thenReturn(labResults);
        when(riskCalculatorService
                .calculateAnemiaRiskPercentage(20.6, 30.6, 10.6, Gender.MALE, false))
                .thenReturn(20.5);

        when(riskCalculatorService
                .calculateDiabetesRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);

        when(riskCalculatorService
                .calculateCKDRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);
        riskService.calculateRiskForTest(testId);

        verify(labResultService).getLabResultsByTestId(testId);
        verify(riskResultRepository, times(3)).save(any());
    }

    @Test
    void shouldThrowIllegalStateException_whenLabResultsEmptyForTest() {
        long testId = 1L;

        when(labResultService.getLabResultsByTestId(testId)).thenReturn(Collections.emptyList());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> riskService.calculateRiskForTest(testId));
        assertEquals("No lab results found for test", ex.getMessage());

        verify(riskResultRepository, never()).save(any());
    }

    @Test
    void shouldReturnLatestRisksByPatientId_whenResultsExist() {
        long patientId = 1L;

        RiskResult entity1 = new RiskResult();
        RiskResult entity2 = new RiskResult();

        RiskResultResponseDto dto1 = new RiskResultResponseDto(1, Disease.ANEMIA, RiskGroup.LOW,
                20.5, LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1));
        RiskResultResponseDto dto2 = new RiskResultResponseDto(1, Disease.DIABETES, RiskGroup.LOW,
                20.5, LocalDate.of(2023, 1, 1),
                LocalDate.of(2025, 1, 1));

        when(patientService.getById(patientId)).thenReturn(new Patient());
        when(riskResultRepository.getLatestByPatientId(patientId)).thenReturn(List.of(entity1, entity2));

        when(riskResultMapper.toDto(entity1)).thenReturn(dto1);
        when(riskResultMapper.toDto(entity2)).thenReturn(dto2);

        List<RiskResultResponseDto> result = riskService.getLatestByPatientId(patientId);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(riskResultRepository).getLatestByPatientId(patientId);
        verify(riskResultMapper).toDto(entity1);
        verify(riskResultMapper).toDto(entity2);
    }

    @Test
    void shouldReturnEmptyList_whenNoRiskResultsFoundForPatient() {
        long patientId = 1L;

        when(patientService.getById(patientId)).thenReturn(new Patient());
        when(riskResultRepository.getLatestByPatientId(patientId)).thenReturn(Collections.emptyList());

        List<RiskResultResponseDto> result = riskService.getLatestByPatientId(patientId);

        assertTrue(result.isEmpty());
        verify(riskResultMapper, never()).toDto(any());
    }

    @Test
    void shouldCalculate3RiskForPatient_whenLabResultsExist() {
        long patientId = 1L;
        Patient patient = new Patient();
        patient.setGender(Gender.MALE);
        patient.setPregnant(false);
        patient.setWeight(60);
        patient.setHeight(180);
        patient.setBirthDate(LocalDate.of(1990, 10, 10));

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        test.setPatient(patient);

        LabResult hgbResult = new LabResult();
        hgbResult.setTest(test);
        hgbResult.setParameter(Parameter.HGB);
        hgbResult.setValue(20.6);

        LabResult hctResult = new LabResult();
        hctResult.setTest(test);
        hctResult.setParameter(Parameter.HCT);
        hctResult.setValue(30.6);

        LabResult rbcResult = new LabResult();
        rbcResult.setTest(test);
        rbcResult.setParameter(Parameter.RBC);
        rbcResult.setValue(10.6);


        List<LabResult> labResults = List.of(hgbResult, hctResult, rbcResult);

        when(labResultService.getLabResultsByPatientId(patientId)).thenReturn(labResults);
        when(riskCalculatorService
                .calculateAnemiaRiskPercentage(20.6, 30.6, 10.6, Gender.MALE, false))
                .thenReturn(20.5);

        when(riskCalculatorService
                .calculateDiabetesRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);

        when(riskCalculatorService
                .calculateCKDRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);
        riskService.calculateRiskForPatient(patientId);

        verify(labResultService).getLabResultsByPatientId(patientId);
        verify(riskResultRepository, times(3)).save(any());
    }

    @Test
    void shouldNotSaveRiskResults_whenLabResultsNotFoundForPatient() {
        long patientId = 1L;
        when(labResultService.getLabResultsByPatientId(patientId)).thenReturn(Collections.emptyList());
        riskService.calculateRiskForPatient(patientId);

        verifyNoInteractions(riskResultRepository, riskCalculatorService);
    }
}
