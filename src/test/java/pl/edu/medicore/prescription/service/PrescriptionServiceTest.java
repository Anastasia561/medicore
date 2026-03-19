package pl.edu.medicore.prescription.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.mapper.PrescriptionMapper;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.prescription.repository.PrescriptionRepository;
import pl.edu.medicore.record.model.Record;
import pl.edu.medicore.record.service.RecordService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private PrescriptionMapper prescriptionMapper;
    @Mock
    private RecordService recordService;
    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @Test
    void shouldCreatePrescription_whenInputIsValid() {
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "Test medicine",
                "10g", LocalDate.of(2026, 10, 2), null, "daily");

        Record record = new Record();
        Prescription prescription = new Prescription();
        prescription.setId(10L);
        when(recordService.getById(dto.recordId())).thenReturn(record);
        when(prescriptionMapper.toEntity(dto, record)).thenReturn(prescription);
        when(prescriptionRepository.save(prescription)).thenReturn(prescription);

        long result = prescriptionService.create(dto);

        assertEquals(10L, result);

        verify(recordService).getById(dto.recordId());
        verify(prescriptionMapper).toEntity(dto, record);
        verify(prescriptionRepository).save(prescription);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartDateAfterEndDateForCreate() {
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "Test medicine",
                "10g", LocalDate.of(2026, 10, 2),
                LocalDate.of(2026, 10, 1), "daily");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> prescriptionService.create(dto));

        assertEquals("End date must be after start date", ex.getMessage());
        verifyNoInteractions(prescriptionRepository, recordService);
    }
}
