package pl.edu.medicore.application.appointment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.consultation.Consultation;
import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.common.exception.DoctorNotAvailableException;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.application.email.EmailType;
import pl.edu.medicore.common.exception.AppointmentCancellationConflictException;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.common.config.properties.SchedulingProperties;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class AppointmentServiceImpl implements AppointmentService {
    private final PersonService personService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final SchedulingProperties schedulingProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<AppointmentInfoDto> getAppointmentsInRange(AppointmentFilterDto filter, Pageable pageable) {
        Role role = personService.getRoleByPublicId(filter.userId());

        if (filter.endDate().isBefore(filter.startDate()))
            throw new IllegalArgumentException("End date must be after start date");

        Page<Appointment> all = appointmentRepository
                .findAll(AppointmentSpecification.withFilter(filter), pageable);

        return role == Role.DOCTOR ? all.map(appointmentMapper::toPatientDto)
                : all.map(appointmentMapper::toDoctorDto);
    }

    @Override
    @Transactional
    public void cancel(UUID id) {
        Appointment appointment = getByPublicId(id);
        if (!appointment.getStatus().equals(Status.SCHEDULED)) {
            throw new AppointmentCancellationConflictException("Appointment can not be cancelled");
        }
        appointment.setStatus(Status.CANCELLED);

        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(appointment);
        eventPublisher.publishEvent(new SendEmailEvent<>(appointment.getPatient().getEmail(),
                EmailType.APPOINTMENT_CANCELLATION, emailDto));
        eventPublisher.publishEvent(new SendEmailEvent<>(appointment.getDoctor().getEmail(),
                EmailType.APPOINTMENT_CANCELLATION, emailDto));
    }

    @Override
    @Transactional
    public UUID create(long patientId, AppointmentCreateDto dto) {
        List<LocalTime> availableTimes = getAvailableTimes(dto.doctorId(), dto.date());
        if (availableTimes.isEmpty() || !availableTimes.contains(dto.time())) {
            throw new IllegalStateException("Selected time slot is not available");
        }

        Doctor doctor = doctorService.getByPublicId(dto.doctorId());
        Patient patient = patientService.getById(patientId);

        Appointment entity = appointmentMapper.toEntity(dto, doctor, patient);
        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(entity);
        eventPublisher.publishEvent(new SendEmailEvent<>(entity.getPatient().getEmail(),
                EmailType.APPOINTMENT_SCHEDULED, emailDto));

        return appointmentRepository.save(entity).getPublicId();
    }

    @Override
    public List<LocalTime> getAvailableTimes(UUID doctorId, LocalDate date) {
        Doctor doctor = doctorService.getByPublicId(doctorId);

        Workday workday = validateWorkday(date);

        Consultation consultation = doctor.getConsultations()
                .stream()
                .filter(c -> c.getWorkday().equals(workday))
                .findFirst()
                .orElseThrow(() -> new DoctorNotAvailableException("Doctor is not available"));

        LocalTime startTime = consultation.getStartTime();
        LocalTime endTime = consultation.getEndTime();

        List<LocalTime> scheduledTimes = appointmentRepository.getScheduledTimesForDoctorAndDate(doctorId, date);

        List<LocalTime> allSlots = new ArrayList<>();
        LocalTime current = startTime;

        while (current.isBefore(endTime)) {
            allSlots.add(current);
            current = current.plusMinutes(schedulingProperties.getSlotDurationMinutes());
        }
        allSlots.removeAll(scheduledTimes);

        return allSlots;
    }

    @Override
    public Appointment getByPublicId(UUID id) {
        return appointmentRepository.findByPublicId(id).orElseThrow(
                () -> new EntityNotFoundException("Appointment not found"));
    }

    @Override
    public long getTotalAppointmentsToday() {
        return appointmentRepository.countByDate(LocalDate.now());
    }

    @Override
    public long getTotalAppointmentsTodayByDoctorId(UUID id) {
        doctorService.checkExistsById(id);
        return appointmentRepository.countByDateAndDoctorPublicId(LocalDate.now(), id);
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatistics() {
        return appointmentRepository.getMonthlyStatistics(LocalDate.now().getYear());
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(UUID id) {
        doctorService.checkExistsById(id);
        return appointmentRepository.getMonthlyStatisticsByDoctorId(id, LocalDate.now().getYear());
    }

    @Override
    public long getDistinctPatientsByDoctorId(UUID doctorId) {
        doctorService.checkExistsById(doctorId);
        return appointmentRepository.countDistinctPatientsByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getAllAppointmentByStatusAndDate(Status status, LocalDate date) {
        return appointmentRepository.findAllByStatusAndDate(status, date);
    }

    @Override
    @Transactional
    public void sendReminderAboutAppointmentsBetween(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from))
            throw new IllegalArgumentException("To date must be after from date");

        List<Appointment> appointments = appointmentRepository.getAppointmentsBetween(from, to);

        for (Appointment app : appointments) {
            app.setReminderSent(true);
            AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(app);
            eventPublisher.publishEvent(new SendEmailEvent<>(app.getPatient().getEmail(),
                    EmailType.UPCOMING_REMINDER, emailDto));
        }
    }

    @Override
    public List<UUID> findIdsForCancellation(long doctorId, Workday dayOfWeek, LocalTime start, LocalTime end) {
        return appointmentRepository.findIdsForCancellation(doctorId, dayOfWeek.name(), start, end);
    }

    private Workday validateWorkday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new DoctorNotAvailableException("Doctor is not available on weekends");
        }
        return Workday.valueOf(date.getDayOfWeek().name());
    }
}
