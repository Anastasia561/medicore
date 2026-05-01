package pl.edu.medicore.consultation.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
    private ConsultationProperties consultationProperties;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ConsultationServiceImpl consultationService;

    @Test
    void shouldReturnConsultationsByDoctorId_whenInputIsValid() {
        UUID doctorId = UUID.randomUUID();
        Consultation consultation = new Consultation();
        ConsultationDto dto = new ConsultationDto(UUID.randomUUID(), Workday.FRIDAY, LocalTime.of(10, 30),
                LocalTime.of(11, 0));

        when(consultationRepository.findByDoctorPublicId(doctorId)).thenReturn(List.of(consultation));
        when(consultationMapper.toDto(consultation)).thenReturn(dto);

        List<ConsultationDto> result = consultationService.findByDoctorId(doctorId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(doctorService).checkExistsById(doctorId);
        verify(consultationRepository).findByDoctorPublicId(doctorId);
        verify(consultationMapper).toDto(consultation);
    }

    @Test
    void shouldReturnEmptyList_whenNoConsultations() {
        UUID doctorId = UUID.randomUUID();

        when(consultationRepository.findByDoctorPublicId(doctorId)).thenReturn(Collections.emptyList());
        List<ConsultationDto> result = consultationService.findByDoctorId(doctorId);

        assertTrue(result.isEmpty());

        verify(doctorService).checkExistsById(doctorId);
        verify(consultationRepository).findByDoctorPublicId(doctorId);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldThrowException_whenDoctorDoesNotExist() {
        UUID doctorId = UUID.randomUUID();

        doThrow(new RuntimeException("Doctor not found")).when(doctorService).checkExistsById(doctorId);
        assertThrows(RuntimeException.class, () -> consultationService.findByDoctorId(doctorId));

        verify(doctorService).checkExistsById(doctorId);
        verifyNoInteractions(consultationRepository);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldReturnConsultationsByDoctorIdAndDate_whenInputIsValid() {
        UUID doctorId = UUID.randomUUID();
        Consultation consultation = new Consultation();

        when(consultationRepository.findByDoctorIdAndWorkday(doctorId, Workday.FRIDAY)).thenReturn(Optional.of(consultation));

        Consultation result = consultationService.findByDoctorIdAndDate(doctorId, LocalDate.of(2026, 3, 20));

        assertEquals(consultation, result);
        verify(doctorService).checkExistsById(doctorId);
        verify(consultationRepository).findByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldThrowDoctorNotAvailableException_whenWorkdayIsWeekend() {
        UUID doctorId = UUID.randomUUID();

        DoctorNotAvailableException ex = assertThrows(DoctorNotAvailableException.class,
                () -> consultationService.findByDoctorIdAndDate(doctorId, LocalDate.of(2026, 3, 21)));

        assertEquals("Doctor is not available on weekends", ex.getMessage());
        verifyNoInteractions(consultationRepository);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldThrowDoctorNotAvailableException_whenDoctorIsNotAvailable() {
        UUID doctorId = UUID.randomUUID();

        when(consultationRepository.findByDoctorIdAndWorkday(doctorId, Workday.FRIDAY)).thenReturn(Optional.empty());

        DoctorNotAvailableException ex = assertThrows(DoctorNotAvailableException.class,
                () -> consultationService.findByDoctorIdAndDate(doctorId, LocalDate.of(2026, 3, 20)));

        assertEquals("Doctor is not available", ex.getMessage());
        verify(doctorService).checkExistsById(doctorId);
        verify(consultationRepository).findByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
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

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(consultationRepository.existsByDoctorIdAndWorkday(doctorId, Workday.FRIDAY)).thenReturn(true);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
                () -> consultationService.create(dto));

        assertEquals("Doctor has consultation schedule for selected day", ex.getMessage());
        verify(consultationRepository).existsByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
        verifyNoInteractions(doctorService, consultationMapper, eventPublisher);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeBeforeStartTimeForCreate() {
        UUID doctorId = UUID.randomUUID();

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(11, 0));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End time must be after start time", ex.getMessage());
        verify(consultationRepository).existsByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeNotInValidRangeForCreate() {
        UUID doctorId = UUID.randomUUID();

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(21, 0));

        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End time must be in valid range", ex.getMessage());
        verify(consultationRepository).existsByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartTimeNotInValidRangeForCreate() {
        UUID doctorId = UUID.randomUUID();

        ConsultationCreateDto dto = new ConsultationCreateDto(doctorId, Workday.FRIDAY,
                LocalTime.of(6, 0), LocalTime.of(11, 0));

        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("Start time must be in valid range", ex.getMessage());
        verify(consultationRepository).existsByDoctorIdAndWorkday(doctorId, Workday.FRIDAY);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
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
        doctor.setEmail("doctor@test.com");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Consultation consultation = new Consultation();
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
    void shouldDeleteConsultationSuccessfully() {
        UUID consultationId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@test.com");
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Consultation consultation = new Consultation();
        consultation.setDoctor(doctor);

        ScheduleEmailDto emailDto = new ScheduleEmailDto(Workday.FRIDAY, doctor.getFirstName(), doctor.getLastName());

        when(consultationRepository.findByPublicId(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);

        consultationService.delete(consultationId);

        verify(consultationRepository).findByPublicId(consultationId);
        verify(consultationMapper).toEmailDto(consultation);
        verify(consultationRepository).deleteByPublicId(consultationId);
    }
}
