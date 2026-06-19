package pl.edu.medicore.application.record;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.application.appointment.Appointment;
import pl.edu.medicore.application.appointment.AppointmentStatus;
import pl.edu.medicore.application.appointment.AppointmentService;
import pl.edu.medicore.application.doctor.dto.DoctorForRecordDto;
import pl.edu.medicore.application.doctor.Specialization;
import pl.edu.medicore.application.patient.dto.PatientForRecordDto;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.prescription.Prescription;
import pl.edu.medicore.application.prescription.PrescriptionMapper;
import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordFilterDto;
import pl.edu.medicore.application.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.application.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.application.record.dto.RecordPreviewDto;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private RecordMapper recordMapper;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private PrescriptionMapper prescriptionMapper;
    @InjectMocks
    private RecordServiceImpl recordService;

    @Test
    void shouldReturnRecordDto_whenRecordExists() {
        long id = 1L;
        HashId idHash = HashId.of(id);

        DoctorForRecordDto doctor = new DoctorForRecordDto("fTest", "fLast", Specialization.DERMATOLOGIST);
        PatientForRecordDto patient = new PatientForRecordDto("fTest", "fLast", "test@gmail.com");

        Record record = new Record();
        RecordDto dto = new RecordDto(doctor, patient, LocalDate.of(2026, 10, 2), "Test",
                "Test summary", List.of());

        when(recordRepository.findById(id)).thenReturn(Optional.of(record));
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDto result = recordService.getById(idHash);

        assertEquals(result, dto);
        verify(recordRepository).findById(id);
        verify(recordMapper).toDto(record);
        verifyNoInteractions(appointmentService);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenRecordNotFoundByAppointmentId() {
        long id = 1L;
        HashId idHash = HashId.of(id);

        when(recordRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> recordService.getById(idHash));

        assertEquals("Record not found", ex.getMessage());
        verify(recordRepository).findById(id);
        verifyNoInteractions(recordMapper, appointmentService);
    }

    @Test
    void shouldReturnRecordDtoById_whenRecordExists() {
        long id = 1L;
        HashId hashId = HashId.of(id);
        Record record = new Record();

        when(recordRepository.findById(id)).thenReturn(Optional.of(record));

        Record result = recordService.getRecordById(hashId);

        assertEquals(result, record);
        verify(recordRepository).findById(id);
        verifyNoInteractions(appointmentService, recordMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenRecordNotFoundById() {
        long id = 1L;
        HashId hashId = HashId.of(id);

        when(recordRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> recordService.getRecordById(hashId));

        assertEquals("Record not found", ex.getMessage());
        verify(recordRepository).findById(id);
        verifyNoInteractions(recordMapper, appointmentService);
    }

    @Test
    void shouldCreateRecord_whenAppointmentNotCompleted() {
        long appointmentId = 1L;
        HashId appointmentHash = HashId.of(appointmentId);
        PrescriptionCreateDto prescriptionDto = new PrescriptionCreateDto("medicine", "dosage",
                LocalDate.of(2027, 10, 2), null, "freq");
        Prescription prescription = new Prescription();

        RecordCreateDto dto = new RecordCreateDto(appointmentHash, "test diagnosis", "test summary",
                List.of(prescriptionDto));

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Record record = new Record();
        record.setId(10L);

        when(prescriptionMapper.toEntity(prescriptionDto, record)).thenReturn(prescription);
        when(appointmentService.getById(appointmentHash)).thenReturn(appointment);
        when(recordMapper.toEntity(dto, appointment)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);

        recordService.create(dto);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);

        verify(prescriptionMapper).toEntity(prescriptionDto, record);
        verify(appointmentService).getById(appointmentHash);
        verify(recordMapper).toEntity(dto, appointment);
        verify(recordRepository).save(record);
    }

    @Test
    void shouldThrowIllegalStateException_whenAppointmentAlreadyCompleted() {
        long appointmentId = 1L;
        HashId appointmentHash = HashId.of(appointmentId);

        RecordCreateDto dto = new RecordCreateDto(appointmentHash, "test diagnosis", "test summary",
                List.of());

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentService.getById(appointmentHash)).thenReturn(appointment);

        IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
                () -> recordService.create(dto));

        assertEquals("Appointment is already completed", ex.getMessage());
        verify(recordRepository, never()).save(any());
        verify(recordMapper, never()).toEntity(any(), any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartDateAfterEndDate() {
        RecordFilterDto filter = mock(RecordFilterDto.class);
        Pageable pageable = Pageable.unpaged();

        when(filter.startDate()).thenReturn(LocalDate.now());
        when(filter.endDate()).thenReturn(LocalDate.now().minusDays(1));

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> recordService.getAllByPersonId(HashId.of(1L), Role.PATIENT, filter, pageable));

        assertEquals("Start date should be before end date", ex.getMessage());
        verifyNoInteractions(recordMapper, recordRepository);
    }

    @Test
    void shouldMapToDoctorPreviewDto_whenRoleIsDoctor() {
        RecordFilterDto filter = mock(RecordFilterDto.class);
        Pageable pageable = PageRequest.of(0, 5);

        when(filter.startDate()).thenReturn(LocalDate.of(2026, 10, 2));
        when(filter.endDate()).thenReturn(LocalDate.of(2026, 10, 10));

        Record record = new Record();
        Page<Record> records = new PageImpl<>(List.of(record));
        RecordForDoctorPreviewDto dto = new RecordForDoctorPreviewDto();

        when(recordRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(records);
        when(recordMapper.toDoctorPreviewDto(record)).thenReturn(dto);

        Page<RecordPreviewDto> result = recordService.getAllByPersonId(HashId.of(1L), Role.DOCTOR, filter, pageable);

        assertThat(result.getContent().size()).isEqualTo(1);
        verify(recordMapper).toDoctorPreviewDto(record);
        verify(recordMapper, never()).toPatientPreviewDto(any());
    }

    @Test
    void shouldMapToPatientPreviewDto_whenRoleIsPatient() {
        RecordFilterDto filter = mock(RecordFilterDto.class);
        Pageable pageable = PageRequest.of(0, 5);

        when(filter.startDate()).thenReturn(LocalDate.of(2026, 10, 2));
        when(filter.endDate()).thenReturn(LocalDate.of(2026, 10, 10));

        Record record = new Record();
        Page<Record> records = new PageImpl<>(List.of(record));
        RecordForPatientPreviewDto dto = new RecordForPatientPreviewDto();

        when(recordRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(records);
        when(recordMapper.toPatientPreviewDto(record)).thenReturn(dto);

        Page<RecordPreviewDto> result = recordService.getAllByPersonId(HashId.of(1L), Role.PATIENT, filter, pageable);

        assertThat(result.getContent().size()).isEqualTo(1);
        verify(recordMapper).toPatientPreviewDto(record);
        verify(recordMapper, never()).toDoctorPreviewDto(any());
    }
}
