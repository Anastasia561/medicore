package pl.edu.medicore.appointment.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.mapper.AppointmentMapper;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.repository.AppointmentRepository;
import pl.edu.medicore.config.properties.SchedulingProperties;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.service.ConsultationService;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.exception.AppointmentAlreadyCancelledException;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock
    private PersonService personService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private SchedulingProperties schedulingProperties;
    @Mock
    private ConsultationService consultationService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void shouldGetAppointmentsInRangeForPatient_whenInputIsValid() {
        Long userId = 1L;
        AppointmentFilterDto filter = new AppointmentFilterDto(userId, LocalDate.now(), LocalDate.now().plusDays(1),
                Status.SCHEDULED, Specialization.CARDIOLOGIST);
        Pageable pageable = PageRequest.of(0, 10);

        when(personService.getRoleById(userId)).thenReturn(Role.DOCTOR);

        Appointment appointment = new Appointment();
        Page<Appointment> appointmentsPage = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(appointmentsPage);

        AppointmentForPatientDto patientDto = new AppointmentForPatientDto();
        when(appointmentMapper.toPatientDto(appointment)).thenReturn(patientDto);

        Page<AppointmentInfoDto> result = appointmentService.getAppointmentsInRange(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(patientDto, result.getContent().get(0));
        verify(appointmentMapper, times(1)).toPatientDto(appointment);
        verify(appointmentMapper, never()).toDoctorDto(any());
    }

    @Test
    void shouldGetAppointmentsInRangeForDoctor_whenInputIsValid() {
        Long userId = 1L;
        AppointmentFilterDto filter = new AppointmentFilterDto(userId, LocalDate.now(), LocalDate.now().plusDays(1),
                Status.SCHEDULED, Specialization.CARDIOLOGIST);
        Pageable pageable = PageRequest.of(0, 10);

        when(personService.getRoleById(userId)).thenReturn(Role.PATIENT);

        Appointment appointment = new Appointment();
        Page<Appointment> appointmentsPage = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(appointmentsPage);

        AppointmentForDoctorDto doctorDto = new AppointmentForDoctorDto();
        when(appointmentMapper.toDoctorDto(appointment)).thenReturn(doctorDto);

        Page<AppointmentInfoDto> result = appointmentService.getAppointmentsInRange(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(doctorDto, result.getContent().get(0));
        verify(appointmentMapper, times(1)).toDoctorDto(appointment);
        verify(appointmentMapper, never()).toPatientDto(any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenInvalidDateRange() {
        Long userId = 1L;
        AppointmentFilterDto filter = new AppointmentFilterDto(userId, LocalDate.now(), LocalDate.now().minusDays(1),
                Status.SCHEDULED, Specialization.CARDIOLOGIST);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.getAppointmentsInRange(filter, PageRequest.of(0, 10)));

        assertEquals("End date must be after start date", exception.getMessage());
    }

    @Test
    void shouldCancelAppointment_whenNotAlreadyCancelled() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setStatus(Status.SCHEDULED);
        Patient patient = new Patient();
        patient.setEmail("patient@example.com");
        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@example.com");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        AppointmentNotificationEmailDto emailDto = new AppointmentNotificationEmailDto("PF",
                "PL", "DF", "DL", Specialization.CARDIOLOGIST,
                "2026-10-10", "10:30");

        when(appointmentRepository.findById(appointmentId)).thenReturn(java.util.Optional.of(appointment));
        when(appointmentMapper.toEmailDto(appointment)).thenReturn(emailDto);

        appointmentService.cancel(appointmentId);

        assertEquals(Status.CANCELLED, appointment.getStatus());
        verify(eventPublisher, times(2)).publishEvent(any(SendEmailEvent.class));
    }

    @Test
    void shouldThrowAppointmentAlreadyCancelledException_whenAlreadyCancelled() {
        Long appointmentId = 2L;
        Appointment appointment = new Appointment();
        appointment.setStatus(Status.CANCELLED);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentAlreadyCancelledException exception = assertThrows(
                AppointmentAlreadyCancelledException.class,
                () -> appointmentService.cancel(appointmentId)
        );
        assertEquals("Appointment is already cancelled", exception.getMessage());

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldCreateAppointment_whenInputIsValid() {
        Long patientId = 1L;
        Long doctorId = 2L;
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        AppointmentCreateDto dto = new AppointmentCreateDto(doctorId, date, time);

        AppointmentService spyService = spy(appointmentService);
        doReturn(List.of(time)).when(spyService).getAvailableTimes(doctorId, date);

        Doctor doctor = new Doctor();
        Patient patient = new Patient();
        Appointment entity = new Appointment();
        entity.setPatient(patient);
        entity.setDoctor(doctor);
        entity.setId(100L);

        AppointmentNotificationEmailDto emailDto = new AppointmentNotificationEmailDto("PF",
                "PL", "DF", "DL", Specialization.CARDIOLOGIST,
                "2026-10-10", "10:30");

        when(doctorService.getById(doctorId)).thenReturn(doctor);
        when(patientService.getById(patientId)).thenReturn(patient);
        when(appointmentMapper.toEntity(dto, doctor, patient)).thenReturn(entity);
        when(appointmentMapper.toEmailDto(entity)).thenReturn(emailDto);
        when(appointmentRepository.save(entity)).thenReturn(entity);

        long resultId = spyService.create(patientId, dto);

        assertEquals(100L, resultId);
        verify(eventPublisher, times(1)).publishEvent(any(SendEmailEvent.class));
        verify(appointmentRepository, times(1)).save(entity);
    }

    @Test
    void shouldThrowIllegalStateException_whenNoAvailableTimes() {
        Long patientId = 1L;
        Long doctorId = 2L;
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime requestedTime = LocalTime.of(10, 0);

        AppointmentCreateDto dto = new AppointmentCreateDto(doctorId, date, requestedTime);

        AppointmentService spyService = spy(appointmentService);
        doReturn(List.of(LocalTime.of(9, 0), LocalTime.of(11, 0)))
                .when(spyService).getAvailableTimes(doctorId, date);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> spyService.create(patientId, dto));

        assertEquals("Selected time slot is not available", exception.getMessage());
        verifyNoInteractions(appointmentRepository, eventPublisher);
    }

    @Test
    void shouldReturnAvailableTimes_whenScheduledAppointmentsExists() {
        Long doctorId = 1L;
        LocalDate date = LocalDate.now();

        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        when(consultationService.findByDoctorIdAndDate(doctorId, date)).thenReturn(consultation);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);

        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(List.of(LocalTime.of(9, 30)));

        List<LocalTime> availableTimes = appointmentService.getAvailableTimes(doctorId, date);

        assertEquals(1, availableTimes.size());
        assertEquals(LocalTime.of(9, 0), availableTimes.get(0));
    }

    @Test
    void shouldReturnAvailableTimes_whenNoScheduledAppointments() {
        Long doctorId = 1L;
        LocalDate date = LocalDate.now();

        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        when(consultationService.findByDoctorIdAndDate(doctorId, date)).thenReturn(consultation);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);

        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(List.of());

        List<LocalTime> availableTimes = appointmentService.getAvailableTimes(doctorId, date);

        assertEquals(2, availableTimes.size());
        assertEquals(LocalTime.of(9, 0), availableTimes.get(0));
        assertEquals(LocalTime.of(9, 30), availableTimes.get(1));
    }

    @Test
    void shouldReturnEmptyList_whenAllSlotsAreBooked() {
        Long doctorId = 1L;
        LocalDate date = LocalDate.now();

        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        when(consultationService.findByDoctorIdAndDate(doctorId, date)).thenReturn(consultation);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);

        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(List.of(LocalTime.of(9, 0), LocalTime.of(9, 30)));

        List<LocalTime> availableTimes = appointmentService.getAvailableTimes(doctorId, date);

        assertTrue(availableTimes.isEmpty());
    }

    @Test
    void shouldReturnAppointmentById_whenAppointmentExists() {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        assertEquals(appointment, appointmentService.getById(1L));
    }

    @Test
    void shouldThrowEntityNotFoundException_whenAppointmentDoesNotExist() {
        Long id = 100L;
        when(appointmentRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.getById(id));

        assertEquals("Appointment not found", ex.getMessage());
    }

    @Test
    void shouldGetTotalAppointmentsToday_whenAppointmentsExist() {
        when(appointmentRepository.countByDate(LocalDate.now())).thenReturn(1L);

        assertEquals(1L, appointmentService.getTotalAppointmentsToday());
    }

    @Test
    void shouldGetTotalAppointmentsTodayByDoctorId_whenAppointmentsExist() {
        when(appointmentRepository.countByDateAndDoctorId(LocalDate.now(), 1L)).thenReturn(1L);

        assertEquals(1L, appointmentService.getTotalAppointmentsTodayByDoctorId(1L));
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistForTotalTodayAppointments() {
        long doctorId = 1L;
        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(doctorId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getTotalAppointmentsTodayByDoctorId(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(doctorId);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldGetMonthlyStatistics_whenAppointmentsExist() {
        List<ConsultationStatisticsDto> list = new ArrayList<>();
        when(appointmentRepository.getMonthlyStatistics(2026)).thenReturn(list);

        assertEquals(list, appointmentService.getMonthlyStatistics());
    }

    @Test
    void shouldReturnMonthlyStatisticsByDoctorId_whenAppointmentsExist() {
        long doctorId = 1L;
        List<ConsultationStatisticsDto> stats = List.of(
                new ConsultationStatisticsDto(1, Status.SCHEDULED, 10),
                new ConsultationStatisticsDto(2, Status.COMPLETED, 4)
        );

        when(appointmentRepository.getMonthlyStatisticsByDoctorId(doctorId, LocalDate.now().getYear()))
                .thenReturn(stats);

        List<ConsultationStatisticsDto> result = appointmentService.getMonthlyStatisticsByDoctorId(doctorId);

        assertEquals(stats.size(), result.size());
        assertEquals(stats, result);
        verify(doctorService, times(1)).checkExistsById(doctorId);
        verify(appointmentRepository, times(1))
                .getMonthlyStatisticsByDoctorId(doctorId, LocalDate.now().getYear());
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExist() {
        long doctorId = 1L;
        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(doctorId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getMonthlyStatisticsByDoctorId(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(doctorId);
        verifyNoInteractions(appointmentRepository);
    }


    @Test
    void shouldCountDistinctPatientsByDoctorId_whenAppointmentsExist() {
        long doctorId = 1L;
        when(appointmentRepository.countDistinctPatientsByDoctorId(doctorId)).thenReturn(5L);

        long result = appointmentService.getDistinctPatientsByDoctorId(doctorId);

        assertEquals(5L, result);
        verify(doctorService, times(1)).checkExistsById(doctorId);
        verify(appointmentRepository, times(1)).countDistinctPatientsByDoctorId(doctorId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistForDistinctPatientCount() {
        long doctorId = 1L;
        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(doctorId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getDistinctPatientsByDoctorId(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(doctorId);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void shouldSendAppointmentReminders_whenInputIsValid() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusHours(1);

        Appointment appointment1 = new Appointment();
        Patient patient1 = new Patient();
        patient1.setEmail("patient1@example.com");
        appointment1.setPatient(patient1);

        Appointment appointment2 = new Appointment();
        Patient patient2 = new Patient();
        patient2.setEmail("patient2@example.com");
        appointment2.setPatient(patient2);
        AppointmentNotificationEmailDto emailDto = new AppointmentNotificationEmailDto("PF",
                "PL", "DF", "DL", Specialization.CARDIOLOGIST,
                "2026-10-10", "10:30");

        List<Appointment> appointments = List.of(appointment1, appointment2);

        when(appointmentRepository.getAppointmentsBetween(from, to)).thenReturn(appointments);
        when(appointmentMapper.toEmailDto(any(Appointment.class))).thenReturn(emailDto);

        appointmentService.sendReminderAboutAppointmentsBetween(from, to);

        assertTrue(appointment1.isReminderSent());
        assertTrue(appointment2.isReminderSent());

        verify(eventPublisher, times(2)).publishEvent(any(SendEmailEvent.class));
    }

    @Test
    void shouldSendNoReminders_whenNoAppointments() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusHours(1);

        when(appointmentRepository.getAppointmentsBetween(from, to)).thenReturn(List.of());

        appointmentService.sendReminderAboutAppointmentsBetween(from, to);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenFromDateIsAfterToDate() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.minusHours(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentService.sendReminderAboutAppointmentsBetween(from, to)
        );

        assertEquals("To date must be after from date", exception.getMessage());

        verifyNoInteractions(appointmentRepository, eventPublisher);
    }
}
