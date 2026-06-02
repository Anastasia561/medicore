package pl.edu.medicore.application.prescription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.application.record.Record;
import pl.edu.medicore.application.record.RecordService;
import pl.edu.medicore.common.encryption.HashId;

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
        PrescriptionCreateDto dto = new PrescriptionCreateDto(HashId.of(1L), "Test medicine",
                "10g", LocalDate.of(2026, 10, 2), null, "daily");

        Record record = new Record();
        Prescription prescription = new Prescription();
        prescription.setId(10L);
        when(recordService.getById(dto.recordId())).thenReturn(record);
        when(prescriptionMapper.toEntity(dto, record)).thenReturn(prescription);
        when(prescriptionRepository.save(prescription)).thenReturn(prescription);

        prescriptionService.create(dto);

        verify(recordService).getById(dto.recordId());
        verify(prescriptionMapper).toEntity(dto, record);
        verify(prescriptionRepository).save(prescription);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartDateAfterEndDateForCreate() {
        PrescriptionCreateDto dto = new PrescriptionCreateDto(HashId.of(1L), "Test medicine",
                "10g", LocalDate.of(2026, 10, 2),
                LocalDate.of(2026, 10, 1), "daily");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> prescriptionService.create(dto));

        assertEquals("End date must be after start date", ex.getMessage());
        verifyNoInteractions(prescriptionRepository, recordService);
    }
}
