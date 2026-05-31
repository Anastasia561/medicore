package pl.edu.medicore.application.appointment;

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
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.application.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.common.config.properties.SchedulingProperties;
import pl.edu.medicore.application.consultation.Consultation;
import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.Specialization;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.exception.AppointmentCancellationConflictException;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @Test
    void shouldGetAppointmentsInRangeForPatient_whenInputIsValid() {
        HashId hashId = HashId.of(1L);
        AppointmentFilterDto filter = new AppointmentFilterDto(hashId, LocalDate.now(), LocalDate.now().plusDays(1),
                AppointmentStatus.COMPLETED, Specialization.CARDIOLOGIST);
        Pageable pageable = PageRequest.of(0, 10);

        Person p = new Person();
        p.setRole(Role.DOCTOR);
        when(personService.getById(hashId)).thenReturn(p);

        Appointment appointment = new Appointment();
        Page<Appointment> appointmentsPage = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(appointmentsPage);

        AppointmentForPatientDto patientDto = new AppointmentForPatientDto();
        when(appointmentMapper.toPatientDto(appointment)).thenReturn(patientDto);

        Page<AppointmentInfoDto> result = appointmentService.getAppointmentsInRange(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(patientDto, result.getContent().getFirst());
        verify(appointmentMapper, times(1)).toPatientDto(appointment);
        verify(appointmentMapper, never()).toDoctorDto(any());
    }

    @Test
    void shouldGetAppointmentsInRangeForDoctor_whenInputIsValid() {
        HashId hashId = HashId.of(1L);
        AppointmentFilterDto filter = new AppointmentFilterDto(hashId, LocalDate.now(), LocalDate.now().plusDays(1),
                AppointmentStatus.SCHEDULED, Specialization.CARDIOLOGIST);
        Pageable pageable = PageRequest.of(0, 10);

        Person p = new Person();
        p.setRole(Role.PATIENT);
        when(personService.getById(hashId)).thenReturn(p);

        Appointment appointment = new Appointment();
        Page<Appointment> appointmentsPage = new PageImpl<>(List.of(appointment));
        when(appointmentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(appointmentsPage);

        AppointmentForDoctorDto doctorDto = new AppointmentForDoctorDto();
        when(appointmentMapper.toDoctorDto(appointment)).thenReturn(doctorDto);

        Page<AppointmentInfoDto> result = appointmentService.getAppointmentsInRange(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(doctorDto, result.getContent().getFirst());
        verify(appointmentMapper, times(1)).toDoctorDto(appointment);
        verify(appointmentMapper, never()).toPatientDto(any());
    }

    @Test
    void shouldThrowIllegalArgumentException_whenInvalidDateRange() {
        HashId hashId = HashId.of(1L);
        AppointmentFilterDto filter = new AppointmentFilterDto(hashId, LocalDate.now(), LocalDate.now().minusDays(1),
                AppointmentStatus.SCHEDULED, Specialization.CARDIOLOGIST);

        Person p = new Person();
        p.setRole(Role.DOCTOR);
        when(personService.getById(hashId)).thenReturn(p);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.getAppointmentsInRange(filter, PageRequest.of(0, 10)));

        assertEquals("End date must be after start date", exception.getMessage());
    }

    @Test
    void shouldCancelAppointment_whenNotAlreadyCancelled() {
        HashId hashId = HashId.of(1L);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        Patient patient = new Patient();
        patient.setEmail("patient@example.com");
        Doctor doctor = new Doctor();
        doctor.setEmail("doctor@example.com");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        AppointmentNotificationEmailDto emailDto = new AppointmentNotificationEmailDto("PF",
                "PL", "DF", "DL", Specialization.CARDIOLOGIST,
                "2026-10-10", "10:30");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toEmailDto(appointment)).thenReturn(emailDto);

        appointmentService.cancel(hashId);

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(eventPublisher, times(2)).publishEvent(any(SendEmailEvent.class));
    }

    @Test
    void shouldThrowAppointmentAlreadyCancelledException_whenAlreadyCancelled() {
        HashId hashId = HashId.of(1L);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentCancellationConflictException exception = assertThrows(
                AppointmentCancellationConflictException.class,
                () -> appointmentService.cancel(hashId)
        );
        assertEquals("Appointment can not be cancelled", exception.getMessage());

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldCreateAppointment_whenInputIsValid() {
        HashId hashId = HashId.of(1L);
        long patientId = 1L;

        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        AppointmentCreateDto dto = new AppointmentCreateDto(hashId, date, time);

        AppointmentService spyService = spy(appointmentService);

        doReturn(List.of(time))
                .when(spyService)
                .getAvailableTimes(hashId, date);

        Doctor doctor = new Doctor();
        Patient patient = new Patient();

        Appointment entity = new Appointment();
        entity.setId(100L);
        entity.setDoctor(doctor);
        entity.setPatient(patient);

        AppointmentNotificationEmailDto emailDto =
                new AppointmentNotificationEmailDto("PF", "PL", "DF", "DL",
                        Specialization.CARDIOLOGIST, "2026-10-10", "10:30");

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(patientService.getById(hashId)).thenReturn(patient);
        when(appointmentMapper.toEntity(dto, doctor, patient)).thenReturn(entity);
        when(appointmentMapper.toEmailDto(entity)).thenReturn(emailDto);
        when(appointmentRepository.save(entity)).thenReturn(entity);

        spyService.create(hashId, dto);

        verify(eventPublisher, times(1)).publishEvent(any(SendEmailEvent.class));
        verify(appointmentRepository, times(1)).save(entity);
    }

    @Test
    void shouldThrowIllegalStateException_whenNoAvailableTimes() {
        HashId patientId = HashId.of(1L);
        HashId doctorId = HashId.of(1L);

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
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        LocalDate date = LocalDate.of(2026, 5, 18);
        Workday workday = Workday.MONDAY;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Consultation consultation = new Consultation();
        consultation.setWorkday(workday);
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        doctor.setConsultations(Set.of(consultation));

        List<LocalTime> scheduledTimes = List.of(LocalTime.of(9, 0));

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);
        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(scheduledTimes);

        List<LocalTime> result = appointmentService.getAvailableTimes(hashId, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(LocalTime.of(9, 30)));
        assertFalse(result.contains(LocalTime.of(9, 0)));

        verify(doctorService).getById(hashId);
        verify(appointmentRepository).getScheduledTimesForDoctorAndDate(doctorId, date);
    }

    @Test
    void shouldReturnAvailableTimes_whenNoScheduledAppointments() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        LocalDate date = LocalDate.of(2026, 5, 18);
        Workday workday = Workday.MONDAY;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Consultation consultation = new Consultation();
        consultation.setWorkday(workday);
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        doctor.setConsultations(Set.of(consultation));

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);
        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(new ArrayList<>());

        List<LocalTime> result = appointmentService.getAvailableTimes(hashId, date);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(LocalTime.of(9, 30)));
        assertTrue(result.contains(LocalTime.of(9, 0)));

        verify(doctorService).getById(hashId);
        verify(appointmentRepository).getScheduledTimesForDoctorAndDate(doctorId, date);
    }

    @Test
    void shouldReturnAvailableTimes_whenAllSlotsAreBooked() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        LocalDate date = LocalDate.of(2026, 5, 18);
        Workday workday = Workday.MONDAY;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Consultation consultation = new Consultation();
        consultation.setWorkday(workday);
        consultation.setStartTime(LocalTime.of(9, 0));
        consultation.setEndTime(LocalTime.of(10, 0));

        doctor.setConsultations(Set.of(consultation));

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(schedulingProperties.getSlotDurationMinutes()).thenReturn(30);
        when(appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date))
                .thenReturn(List.of(LocalTime.of(9, 0), LocalTime.of(9, 30)));

        List<LocalTime> result = appointmentService.getAvailableTimes(hashId, date);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(doctorService).getById(hashId);
        verify(appointmentRepository).getScheduledTimesForDoctorAndDate(doctorId, date);
    }

    @Test
    void shouldReturnAppointmentById_whenAppointmentExists() {
        long appId = 1L;
        HashId hashId = HashId.of(appId);

        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(appId)).thenReturn(Optional.of(appointment));

        assertEquals(appointment, appointmentService.getById(hashId));
    }

    @Test
    void shouldThrowEntityNotFoundException_whenAppointmentDoesNotExist() {
        long appId = 1L;
        HashId hashId = HashId.of(appId);

        when(appointmentRepository.findById(appId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> appointmentService.getById(hashId));

        assertEquals("Appointment not found", ex.getMessage());
    }

    @Test
    void shouldGetTotalAppointmentsToday_whenAppointmentsExist() {
        when(appointmentRepository.countByDate(LocalDate.now())).thenReturn(1L);

        assertEquals(1L, appointmentService.getTotalAppointmentsToday());
    }

    @Test
    void shouldGetTotalAppointmentsTodayByDoctorId_whenAppointmentsExist() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        when(appointmentRepository.countByDateAndDoctorId(LocalDate.now(), doctorId)).thenReturn(1L);

        assertEquals(1L, appointmentService.getTotalAppointmentsTodayByDoctorId(hashId));
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistForTotalTodayAppointments() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(hashId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getTotalAppointmentsTodayByDoctorId(hashId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(hashId);
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
        HashId hashId = HashId.of(doctorId);

        List<ConsultationStatisticsDto> stats = List.of(
                new ConsultationStatisticsDto(1, AppointmentStatus.SCHEDULED, 10),
                new ConsultationStatisticsDto(2, AppointmentStatus.COMPLETED, 4)
        );

        when(appointmentRepository.getMonthlyStatisticsByDoctorId(doctorId, LocalDate.now().getYear()))
                .thenReturn(stats);

        List<ConsultationStatisticsDto> result = appointmentService.getMonthlyStatisticsByDoctorId(hashId);

        assertEquals(stats.size(), result.size());
        assertEquals(stats, result);
        verify(doctorService, times(1)).checkExistsById(hashId);
        verify(appointmentRepository, times(1))
                .getMonthlyStatisticsByDoctorId(doctorId, LocalDate.now().getYear());
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExist() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(hashId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getMonthlyStatisticsByDoctorId(hashId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(hashId);
        verifyNoInteractions(appointmentRepository);
    }


    @Test
    void shouldCountDistinctPatientsByDoctorId_whenAppointmentsExist() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        when(appointmentRepository.countDistinctPatientsByDoctorId(doctorId)).thenReturn(5L);

        long result = appointmentService.getDistinctPatientsByDoctorId(hashId);

        assertEquals(5L, result);
        verify(doctorService, times(1)).checkExistsById(hashId);
        verify(appointmentRepository, times(1)).countDistinctPatientsByDoctorId(doctorId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistForDistinctPatientCount() {
        long doctorId = 1L;
        HashId hashId = HashId.of(doctorId);

        doThrow(new EntityNotFoundException("Doctor not found"))
                .when(doctorService).checkExistsById(hashId);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> appointmentService.getDistinctPatientsByDoctorId(hashId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorService, times(1)).checkExistsById(hashId);
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
