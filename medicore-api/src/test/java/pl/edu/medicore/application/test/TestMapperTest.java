package pl.edu.medicore.application.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.test.dto.TestResponseDto;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashIdMapper;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestMapperTest {
    private TestMapper testMapper;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        testMapper = Mappers.getMapper(TestMapper.class);
        HashIdMapper hashIdMapper = new HashIdMapper();

        Field hashIdMapperField = testMapper.getClass().getDeclaredField("hashIdMapper");
        hashIdMapperField.setAccessible(true);
        hashIdMapperField.set(testMapper, hashIdMapper);
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        TestUploadRequestDto dto = new TestUploadRequestDto(null, LocalDate.of(2026, 10, 10));

        Patient patient = new Patient();
        pl.edu.medicore.application.test.Test test = testMapper.toEntity(dto, patient);

        assertNull(test.getId());
        assertEquals(patient, test.getPatient());
        assertEquals(LocalDate.of(2026, 10, 10), test.getDate());
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(testMapper.toEntity(null, null));
    }

    @Test
    void shouldMapToDto_whenInputIsValid() {
        pl.edu.medicore.application.test.Test test = new pl.edu.medicore.application.test.Test();
        test.setId(5L);
        test.setDate(LocalDate.of(2026, 10, 10));

        TestResponseDto dto = testMapper.toDto(test);

        assertEquals(5L, dto.id().value());
        assertEquals(LocalDate.of(2026, 10, 10), dto.date());
    }

    @Test
    void shouldReturnNullDto_whenEntityIsNull() {
        assertNull(testMapper.toDto(null));
    }
}
