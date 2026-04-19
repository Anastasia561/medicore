package pl.edu.medicore.risk.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.risk.dto.RiskResultResponseDto;
import pl.edu.medicore.risk.model.Disease;
import pl.edu.medicore.risk.model.RiskGroup;
import pl.edu.medicore.risk.model.RiskResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RiskResultMapperTest {
    private RiskResultMapper riskResultMapper;

    @BeforeEach
    void setUp() {
        riskResultMapper = Mappers.getMapper(RiskResultMapper.class);
    }

    @Test
    void shouldMapToDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setId(1L);

        Instant calculateAt = LocalDate.of(2025, 10, 10)
                .atStartOfDay(ZoneId.systemDefault()).toInstant();

        pl.edu.medicore.test.model.Test test = new pl.edu.medicore.test.model.Test();
        test.setDate(LocalDate.of(2025, 1, 1));

        RiskResult riskResult = new RiskResult();
        riskResult.setPatient(patient);
        riskResult.setDisease(Disease.ANEMIA);
        riskResult.setRiskGroup(RiskGroup.LOW);
        riskResult.setRiskPercent(20.5);
        riskResult.setTest(test);
        riskResult.setCalculatedAt(calculateAt);

        RiskResultResponseDto dto = riskResultMapper.toDto(riskResult);

        assertEquals(1, dto.patientId());
        assertEquals(LocalDate.of(2025, 1, 1), dto.testDate());
        assertEquals(Disease.ANEMIA, dto.disease());
        assertEquals(RiskGroup.LOW, dto.riskGroup());
        assertEquals(20.5, dto.riskPercent());
        assertEquals(LocalDate.of(2025, 10, 10), dto.calculatedAt());
    }

    @Test
    void shouldReturnNull_whenEntityIsNull() {
        assertNull(riskResultMapper.toDto(null));
    }
}
