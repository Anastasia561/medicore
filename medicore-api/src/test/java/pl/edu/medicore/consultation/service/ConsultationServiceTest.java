package pl.edu.medicore.consultation.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.mapper.ConsultationMapper;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.model.Workday;
import pl.edu.medicore.consultation.repository.ConsultationRepository;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.email.dto.ScheduleEmailDto;
import pl.edu.medicore.exception.DoctorNotAvailableException;
import pl.edu.medicore.config.properties.ConsultationProperties;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultationServiceTest {
    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private ConsultationMapper consultationMapper;
    @Mock
    private DoctorService doctorService;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private ConsultationProperties consultationProperties;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ConsultationServiceImpl consultationService;

    @Test
    void shouldReturnConsultationsByDoctorId_whenInputIsValid() {
        UUID doctorId = UUID.randomUUID();
        Consultation consultation = new Consultation();
        Doctor doctor = new Doctor();
        doctor.setConsultations(Set.of(consultation));

        ConsultationDto dto = new ConsultationDto(UUID.randomUUID(), Workday.FRIDAY, LocalTime.of(10, 30),
                LocalTime.of(11, 0));

        when(consultationMapper.toDto(consultation)).thenReturn(dto);
        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);
        List<ConsultationDto> result = consultationService.findByDoctorId(doctorId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(doctorService).getByPublicId(doctorId);
        verify(consultationMapper).toDto(consultation);
    }

    @Test
    void shouldReturnEmptyList_whenNoConsultations() {
        UUID doctorId = UUID.randomUUID();

        when(doctorService.getByPublicId(doctorId)).thenReturn(new Doctor());

        List<ConsultationDto> result = consultationService.findByDoctorId(doctorId);
        assertTrue(result.isEmpty());

        verify(doctorService).getByPublicId(doctorId);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldThrowException_whenDoctorDoesNotExist() {
        UUID doctorId = UUID.randomUUID();

        doThrow(new RuntimeException("Doctor not found")).when(doctorService).getByPublicId(doctorId);
        assertThrows(RuntimeException.class, () -> consultationService.findByDoctorId(doctorId));

        verify(doctorService).getByPublicId(doctorId);
        verifyNoInteractions(consultationRepository);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldCreateConsultation_whenInputIsValid() {
        UUID doctorId = UUID.randomUUID();

        ConsultationCreateDto dto = new ConsultationCreateDto(
                doctorId,
                Workday.FRIDAY,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@test.com");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Consultation consultation = new Consultation();
        consultation.setId(100L);
        consultation.setDoctor(doctor);
        consultation.setWorkday(Workday.THURSDAY);

        doctor.setConsultations(Set.of(consultation));

        ScheduleEmailDto emailDto = new ScheduleEmailDto(Workday.FRIDAY, "John", "Doe");

        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);
        when(consultationMapper.toEntity(dto, doctor)).thenReturn(consultation);
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(consultationRepository.save(consultation)).thenReturn(consultation);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));

        consultationService.create(dto);

        verify(doctorService).getByPublicId(doctorId);
        verify(consultationMapper).toEntity(dto, doctor);
        verify(consultationMapper).toEmailDto(consultation);
        verify(consultationRepository).save(consultation);
    }

    @Test
    void shouldThrowEntityExistsExceptionException_whenDayAlreadyExists() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.FRIDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
                () -> consultationService.create(dto));

        assertEquals("Doctor has consultation schedule for selected day", ex.getMessage());
        verifyNoInteractions(consultationMapper, eventPublisher);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeBeforeStartTimeForCreate() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(11, 0));

        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End time must be after start time", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeNotInValidRangeForCreate() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(21, 0));

        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End time must be in valid range", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartTimeNotInValidRangeForCreate() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(6, 0), LocalTime.of(11, 0));

        when(doctorService.getByPublicId(doctorId)).thenReturn(doctor);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("Start time must be in valid range", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenConsultationDoesNotExistForUpdate() {
        UUID consultationId = UUID.randomUUID();

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        when(consultationRepository.findByPublicId(consultationId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> consultationService.update(consultationId, dto));

        assertEquals("Consultation not found", ex.getMessage());
        verify(consultationRepository).findByPublicId(consultationId);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldUpdateConsultationSuccessfully() {
        UUID consultationId = UUID.randomUUID();

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(11, 0));

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setEmail("doctor@test.com");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(12, 0));
        consultation.setEndTime(LocalTime.of(16, 0));
        consultation.setDoctor(doctor);

        ScheduleEmailDto emailDto = new ScheduleEmailDto(Workday.FRIDAY, doctor.getFirstName(), doctor.getLastName());

        when(consultationRepository.findByPublicId(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));

        UUID result = consultationService.update(consultationId, dto);

        assertEquals(consultationId, result);
        verify(consultationRepository).findByPublicId(consultationId);
        verify(consultationMapper).updateConsultationFromDto(dto, consultation);
        verify(consultationMapper).toEmailDto(consultation);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenConsultationDoesNotExistForDelete() {
        UUID consultationId = UUID.randomUUID();

        new ConsultationUpdateDto(LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        when(consultationRepository.findByPublicId(consultationId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> consultationService.delete(consultationId));

        assertEquals("Consultation not found", ex.getMessage());
        verify(consultationRepository).findByPublicId(consultationId);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldDeleteConsultationAndCancelAppointments_whenInputIsValid() {
        UUID consultationId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        long doctorId = 1L;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setEmail("doctor@test.com");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);
        consultation.setWorkday(Workday.FRIDAY);
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(12, 0));

        ScheduleEmailDto emailDto = new ScheduleEmailDto(Workday.FRIDAY, doctor.getFirstName(), doctor.getLastName());

        when(consultationRepository.findByPublicId(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(appointmentService.findIdsForCancellation(doctorId, Workday.FRIDAY, consultation.getStartTime(), consultation.getEndTime()))
                .thenReturn(List.of(appointmentId));

        consultationService.delete(consultationId);

        verify(appointmentService).findIdsForCancellation(doctorId, Workday.FRIDAY, consultation.getStartTime(), consultation.getEndTime());
        verify(appointmentService).cancel(appointmentId);
        verify(consultationRepository).findByPublicId(consultationId);
        verify(consultationRepository).deleteByPublicId(consultationId);
    }
}
