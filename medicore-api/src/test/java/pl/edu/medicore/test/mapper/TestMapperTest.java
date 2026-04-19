package pl.edu.medicore.test.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.test.dto.TestUploadRequestDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestMapperTest {
    private TestMapper testMapper;

    @BeforeEach
    void setUp() {
        testMapper = Mappers.getMapper(TestMapper.class);
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        TestUploadRequestDto dto = new TestUploadRequestDto(null, LocalDate.of(2026, 10, 10));

        Patient patient = new Patient();
        pl.edu.medicore.test.model.Test test = testMapper.toEntity(dto, patient);

        assertNull(test.getId());
        assertEquals(patient, test.getPatient());
        assertEquals(LocalDate.of(2026, 10, 10), test.getDate());
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(testMapper.toEntity(null, null));
    }
}
