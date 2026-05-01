package pl.edu.medicore.record.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.doctor.dto.DoctorForRecordDto;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.patient.dto.PatientForRecordDto;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordFilterDto;
import pl.edu.medicore.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.record.dto.RecordPreviewDto;
import pl.edu.medicore.record.mapper.RecordMapper;
import pl.edu.medicore.record.repository.RecordRepository;
import pl.edu.medicore.record.model.Record;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @InjectMocks
    private RecordServiceImpl recordService;

    @Test
    void shouldReturnRecordDto_whenRecordExists() {
        UUID appointmentId = UUID.randomUUID();
        DoctorForRecordDto doctor = new DoctorForRecordDto("fTest", "fLast", Specialization.DERMATOLOGIST);
        PatientForRecordDto patient = new PatientForRecordDto("fTest", "fLast", "test@gmail.com");

        Record record = new Record();
        RecordDto dto = new RecordDto(doctor, patient, LocalDate.of(2026, 10, 2), "Test",
                "Test summary", List.of());

        when(recordRepository.findByAppointmentPublicId(appointmentId)).thenReturn(Optional.of(record));
        when(recordMapper.toDto(record)).thenReturn(dto);

        RecordDto result = recordService.getByAppointmentId(appointmentId);

        assertEquals(result, dto);
        verify(recordRepository).findByAppointmentPublicId(appointmentId);
        verify(recordMapper).toDto(record);
        verifyNoInteractions(appointmentService);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenRecordNotFoundByAppointmentId() {
        UUID appointmentId = UUID.randomUUID();

        when(recordRepository.findByAppointmentPublicId(appointmentId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> recordService.getByAppointmentId(appointmentId));

        assertEquals("Record not found", ex.getMessage());
        verify(recordRepository).findByAppointmentPublicId(appointmentId);
        verifyNoInteractions(recordMapper, appointmentService);
    }

    @Test
    void shouldReturnRecordDtoById_whenRecordExists() {
        UUID id = UUID.randomUUID();
        Record record = new Record();

        when(recordRepository.findByPublicId(id)).thenReturn(Optional.of(record));

        Record result = recordService.getByPublicId(id);

        assertEquals(result, record);
        verify(recordRepository).findByPublicId(id);
        verifyNoInteractions(appointmentService, recordMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenRecordNotFoundById() {
        UUID id = UUID.randomUUID();

        when(recordRepository.findByPublicId(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> recordService.getByPublicId(id));

        assertEquals("Record not found", ex.getMessage());
        verify(recordRepository).findByPublicId(id);
        verifyNoInteractions(recordMapper, appointmentService);
    }

    @Test
    void shouldCreateRecord_whenAppointmentNotCompleted() {
        UUID appointmentId = UUID.randomUUID();
        RecordCreateDto dto = new RecordCreateDto(appointmentId, "test diagnosis", "test summary");

        Appointment appointment = new Appointment();
        appointment.setStatus(Status.SCHEDULED);

        Record record = new Record();
        record.setId(10L);

        when(appointmentService.getByPublicId(appointmentId)).thenReturn(appointment);
        when(recordMapper.toEntity(dto, appointment)).thenReturn(record);
        when(recordRepository.save(record)).thenReturn(record);

        recordService.create(dto);

        assertThat(appointment.getStatus()).isEqualTo(Status.COMPLETED);

        verify(appointmentService).getByPublicId(appointmentId);
        verify(recordMapper).toEntity(dto, appointment);
        verify(recordRepository).save(record);
    }

    @Test
    void shouldThrowIllegalStateException_whenAppointmentAlreadyCompleted() {
        UUID appointmentId = UUID.randomUUID();
        RecordCreateDto dto = new RecordCreateDto(appointmentId, "test diagnosis", "test summary");

        Appointment appointment = new Appointment();
        appointment.setStatus(Status.COMPLETED);

        when(appointmentService.getByPublicId(appointmentId)).thenReturn(appointment);

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
                () -> recordService.getAllById(1L, Role.PATIENT, filter, pageable));

        assertEquals("Start date should be before end date", ex.getMessage());
        verifyNoInteractions(recordMapper, recordRepository);
    }

    @Test
    void shouldMapToDoctorPreviewDto_whenRoleIsDoctor() {
        RecordFilterDto filter = mock(RecordFilterDto.class);
        Pageable pageable = Pageable.unpaged();

        when(filter.startDate()).thenReturn(LocalDate.of(2026, 10, 2));
        when(filter.endDate()).thenReturn(LocalDate.of(2026, 10, 10));

        Record record = new Record();
        Page<Record> records = new PageImpl<>(List.of(record));
        RecordForDoctorPreviewDto dto = new RecordForDoctorPreviewDto();

        when(recordRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(records);
        when(recordMapper.toDoctorPreviewDto(record)).thenReturn(dto);

        Page<RecordPreviewDto> result = recordService.getAllById(1L, Role.DOCTOR, filter, pageable);

        assertThat(result.getContent().size()).isEqualTo(1);

        verify(recordMapper).toDoctorPreviewDto(record);
        verify(recordMapper, never()).toPatientPreviewDto(any());
    }

    @Test
    void shouldMapToPatientPreviewDto_whenRoleIsDoctor() {
        RecordFilterDto filter = mock(RecordFilterDto.class);
        Pageable pageable = Pageable.unpaged();

        when(filter.startDate()).thenReturn(LocalDate.of(2026, 10, 2));
        when(filter.endDate()).thenReturn(LocalDate.of(2026, 10, 10));

        Record record = new Record();
        Page<Record> records = new PageImpl<>(List.of(record));
        RecordForPatientPreviewDto dto = new RecordForPatientPreviewDto();

        when(recordRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(records);
        when(recordMapper.toPatientPreviewDto(record)).thenReturn(dto);

        Page<RecordPreviewDto> result = recordService.getAllById(1L, Role.PATIENT, filter, pageable);

        assertThat(result.getContent().size()).isEqualTo(1);

        verify(recordMapper).toPatientPreviewDto(record);
        verify(recordMapper, never()).toDoctorPreviewDto(any());
    }
}
