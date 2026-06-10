package pl.edu.medicore.application.consultation;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.application.appointment.AppointmentService;
import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.email.dto.ScheduleEmailDto;
import pl.edu.medicore.common.config.properties.ConsultationProperties;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        HashId hashId = new HashId(1L);

        Consultation consultation = new Consultation();
        Doctor doctor = new Doctor();
        doctor.setConsultations(Set.of(consultation));

        ConsultationDto dto = new ConsultationDto(hashId, Workday.FRIDAY, LocalTime.of(10, 30),
                LocalTime.of(11, 0));

        when(consultationMapper.toDto(consultation)).thenReturn(dto);
        when(doctorService.getById(hashId)).thenReturn(doctor);
        List<ConsultationDto> result = consultationService.findByDoctorId(hashId);

        assertEquals(1, result.size());
        assertEquals(dto, result.getFirst());

        verify(doctorService).getById(hashId);
        verify(consultationMapper).toDto(consultation);
    }

    @Test
    void shouldReturnEmptyList_whenNoConsultations() {
        HashId hashId = HashId.of(1L);

        when(doctorService.getById(hashId)).thenReturn(new Doctor());

        List<ConsultationDto> result = consultationService.findByDoctorId(hashId);
        assertTrue(result.isEmpty());

        verify(doctorService).getById(hashId);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldThrowException_whenDoctorDoesNotExist() {
        HashId hashId = HashId.of(1L);

        doThrow(new RuntimeException("Doctor not found")).when(doctorService).getById(hashId);
        assertThrows(RuntimeException.class, () -> consultationService.findByDoctorId(hashId));

        verify(doctorService).getById(hashId);
        verifyNoInteractions(consultationRepository);
        verifyNoInteractions(consultationMapper);
    }

    @Test
    void shouldCreateConsultation_whenInputIsValid() {
        HashId hashId = HashId.of(1L);

        ConsultationCreateDto dto = new ConsultationCreateDto(
                hashId,
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

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(consultationMapper.toEntity(dto, doctor)).thenReturn(consultation);
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(consultationRepository.save(consultation)).thenReturn(consultation);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));

        consultationService.create(dto);

        verify(doctorService).getById(hashId);
        verify(consultationMapper).toEntity(dto, doctor);
        verify(consultationMapper).toEmailDto(consultation);
        verify(consultationRepository).save(consultation);
    }

    @Test
    void shouldThrowEntityExistsExceptionException_whenDayAlreadyExists() {
        HashId hashId = HashId.of(1L);

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.FRIDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(hashId, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(doctorService.getById(hashId)).thenReturn(doctor);

        EntityExistsException ex = assertThrows(EntityExistsException.class,
                () -> consultationService.create(dto));

        assertEquals("Doctor has consultation schedule for selected day", ex.getMessage());
        verifyNoInteractions(consultationMapper, eventPublisher);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeBeforeStartTimeForCreate() {
        HashId hashId = HashId.of(1L);

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(hashId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(11, 0));

        when(doctorService.getById(hashId)).thenReturn(doctor);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End startTime must be after start startTime", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenEndTimeNotInValidRangeForCreate() {
        HashId hashId = HashId.of(1L);

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(hashId, Workday.FRIDAY,
                LocalTime.of(12, 0), LocalTime.of(21, 0));

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("End startTime must be in valid range", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenStartTimeNotInValidRangeForCreate() {
        HashId hashId = HashId.of(1L);

        Doctor doctor = new Doctor();
        Consultation c = new Consultation();
        c.setWorkday(Workday.THURSDAY);
        doctor.setConsultations(Set.of(c));

        ConsultationCreateDto dto = new ConsultationCreateDto(hashId, Workday.FRIDAY,
                LocalTime.of(6, 0), LocalTime.of(11, 0));

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> consultationService.create(dto));

        assertEquals("Start startTime must be in valid range", ex.getMessage());
        verifyNoInteractions(eventPublisher, consultationMapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenConsultationDoesNotExistForUpdate() {
        long consultationId = 1L;
        HashId hashId = HashId.of(consultationId);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        when(consultationRepository.findById(consultationId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> consultationService.update(hashId, dto));

        assertEquals("Consultation not found", ex.getMessage());
        verify(consultationRepository).findById(consultationId);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldUpdateConsultationSuccessfully() {
        long consultationId = 1L;
        HashId hashId = HashId.of(consultationId);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0),
                LocalTime.of(11, 0));

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

        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(consultationProperties.getEnd()).thenReturn(LocalTime.of(18, 0));
        when(consultationProperties.getStart()).thenReturn(LocalTime.of(8, 0));

        HashId result = consultationService.update(hashId, dto);

        assertEquals(hashId, result);
        verify(consultationRepository).findById(consultationId);
        verify(consultationMapper).updateConsultationFromDto(dto, consultation);
        verify(consultationMapper).toEmailDto(consultation);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenConsultationDoesNotExistForDelete() {
        long consultationId = 1L;
        HashId hashId = HashId.of(consultationId);

        new ConsultationUpdateDto(LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        when(consultationRepository.findById(consultationId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> consultationService.delete(hashId));

        assertEquals("Consultation not found", ex.getMessage());
        verify(consultationRepository).findById(consultationId);
        verifyNoInteractions(doctorService, eventPublisher, consultationMapper);
    }

    @Test
    void shouldDeleteConsultationAndCancelAppointments_whenInputIsValid() {
        long consultationId = 1L;
        HashId consultationHash = HashId.of(consultationId);
        HashId appointmentId = HashId.of(1L);
        HashId doctorHash = HashId.of(1L);
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

        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationMapper.toEmailDto(consultation)).thenReturn(emailDto);
        when(appointmentService.findIdsForCancellation(doctorHash, Workday.FRIDAY, consultation.getStartTime(), consultation.getEndTime()))
                .thenReturn(List.of(appointmentId));

        consultationService.delete(consultationHash);

        verify(appointmentService).findIdsForCancellation(doctorHash, Workday.FRIDAY, consultation.getStartTime(), consultation.getEndTime());
        verify(appointmentService).cancel(appointmentId);
        verify(consultationRepository).findById(consultationId);
        verify(consultationRepository).deleteById(consultationId);
    }
}
