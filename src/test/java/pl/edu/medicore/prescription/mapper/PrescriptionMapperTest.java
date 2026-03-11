package pl.edu.medicore.prescription.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.record.model.Record;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PrescriptionMapperTest {
    private PrescriptionMapper prescriptionMapper;

    @BeforeEach
    void setUp() {
        prescriptionMapper = Mappers.getMapper(PrescriptionMapper.class);
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "Test medicine",
                "10g", LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 12, 2), "daily");

        Record record = new Record();

        Prescription entity = prescriptionMapper.toEntity(dto, record);
        assertEquals("Test medicine", entity.getMedicine());
        assertEquals("10g", entity.getDosage());
        assertEquals("daily", entity.getFrequency());
        assertEquals(LocalDate.of(2026, 10, 10), entity.getStartDate());
        assertEquals(LocalDate.of(2026, 12, 2), entity.getEndDate());
        assertEquals(record, entity.getRecord());
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(prescriptionMapper.toEntity(null, null));
    }
}
