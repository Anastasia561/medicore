package pl.edu.medicore.application.risk.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.application.labresult.LabResult;
import pl.edu.medicore.application.labresult.Parameter;
import pl.edu.medicore.application.labresult.LabResultService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.risk.Disease;
import pl.edu.medicore.application.risk.RiskGroup;
import pl.edu.medicore.application.risk.RiskResult;
import pl.edu.medicore.application.risk.RiskResultMapper;
import pl.edu.medicore.application.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.application.test.TestService;
import pl.edu.medicore.common.encryption.HashId;

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
    private RiskResultRepository riskResultRepository;
    @Mock
    private RiskResultMapper riskResultMapper;
    @Mock
    private PatientService patientService;
    @Mock
    private TestService testService;
    @InjectMocks
    private RiskResultServiceImpl riskService;

    @Test
    void shouldCalculate3RiskForTest_whenLabResultsExist() {
        long testId = 1L;
        HashId hashId = new HashId(testId);

        Patient patient = new Patient();
        patient.setGender(Gender.MALE);
        patient.setPregnant(false);
        patient.setWeight(60);
        patient.setHeight(180);
        patient.setBirthDate(LocalDate.of(1990, 10, 10));

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
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

        when(labResultService.getLabResultsByTestId(hashId)).thenReturn(labResults);
        when(riskCalculatorService
                .calculateAnemiaRiskPercentage(20.6, 30.6, 10.6, Gender.MALE, false))
                .thenReturn(20.5);

        when(riskCalculatorService
                .calculateDiabetesRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);

        when(riskCalculatorService
                .calculateCKDRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);
        riskService.calculateRiskForTest(hashId);

        verify(labResultService).getLabResultsByTestId(hashId);
        verify(riskResultRepository, times(3)).save(any());
    }

    @Test
    void shouldThrowIllegalStateException_whenLabResultsEmptyForTest() {
        long testId = 1L;
        HashId hashId = new HashId(testId);

        when(labResultService.getLabResultsByTestId(hashId)).thenReturn(Collections.emptyList());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> riskService.calculateRiskForTest(hashId));
        assertEquals("No lab results found for test", ex.getMessage());

        verify(riskResultRepository, never()).save(any());
    }

    @Test
    void shouldReturnLatestRisksByPatientId_whenResultsExist() {
        long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        RiskResult entity1 = new RiskResult();
        RiskResult entity2 = new RiskResult();

        RiskResultResponseDto dto1 = new RiskResultResponseDto(HashId.of(1L), Disease.ANEMIA, RiskGroup.LOW,
                20.5, LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 1));
        RiskResultResponseDto dto2 = new RiskResultResponseDto(HashId.of(1L), Disease.DIABETES, RiskGroup.LOW,
                20.5, LocalDate.of(2023, 1, 1),
                LocalDate.of(2025, 1, 1));

        when(riskResultRepository.getLatestByPatientPublicId(patientId)).thenReturn(List.of(entity1, entity2));

        when(riskResultMapper.toDto(entity1)).thenReturn(dto1);
        when(riskResultMapper.toDto(entity2)).thenReturn(dto2);

        List<RiskResultResponseDto> result = riskService.getLatestByPatientId(patientHash);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(riskResultRepository).getLatestByPatientPublicId(patientId);
        verify(riskResultMapper).toDto(entity1);
        verify(riskResultMapper).toDto(entity2);
    }

    @Test
    void shouldReturnEmptyList_whenNoRiskResultsFoundForPatient() {
        long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        when(riskResultRepository.getLatestByPatientPublicId(patientId)).thenReturn(Collections.emptyList());

        List<RiskResultResponseDto> result = riskService.getLatestByPatientId(patientHash);

        assertTrue(result.isEmpty());
        verify(riskResultMapper, never()).toDto(any());
    }

    @Test
    void shouldCalculate3RiskForPatient_whenLabResultsExist() {
        long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        Patient patient = new Patient();
        patient.setGender(Gender.MALE);
        patient.setPregnant(false);
        patient.setWeight(60);
        patient.setHeight(180);
        patient.setBirthDate(LocalDate.of(1990, 10, 10));

        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
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

        when(labResultService.getLabResultsByPatientId(patientHash)).thenReturn(labResults);
        when(riskCalculatorService
                .calculateAnemiaRiskPercentage(20.6, 30.6, 10.6, Gender.MALE, false))
                .thenReturn(20.5);

        when(riskCalculatorService
                .calculateDiabetesRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);

        when(riskCalculatorService
                .calculateCKDRisk(any(), any(), any(), any(), any()))
                .thenReturn(null);
        riskService.calculateRiskForPatient(patientHash);

        verify(labResultService).getLabResultsByPatientId(patientHash);
        verify(riskResultRepository, times(3)).save(any());
    }

    @Test
    void shouldNotSaveRiskResults_whenLabResultsNotFoundForPatient() {
        long patientId = 1L;
        HashId patientHash = new HashId(patientId);

        when(labResultService.getLabResultsByPatientId(patientHash)).thenReturn(Collections.emptyList());
        riskService.calculateRiskForPatient(patientHash);

        verifyNoInteractions(riskResultRepository, riskCalculatorService);
    }
}
