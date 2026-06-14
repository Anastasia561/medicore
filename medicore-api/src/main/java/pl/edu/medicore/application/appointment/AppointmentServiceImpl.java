package pl.edu.medicore.application.appointment;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.consultation.Consultation;
import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.common.encryption.HashId;
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
    public List<? extends AppointmentInfoDto> getAppointmentsInRange(HashId userId, AppointmentFilterDto filter) {
        Role role = personService.getById(userId).getRole();

        if (filter.endDate().isBefore(filter.startDate()))
            throw new IllegalArgumentException("End date must be after start date");

        Sort sorting = Sort.by(Sort.Direction.ASC, "date", "startTime");

        List<Appointment> all = appointmentRepository
                .findAll(AppointmentSpecification.withFilter(userId.value(), filter), sorting);

        return role == Role.PATIENT ? all.stream().map(appointmentMapper::toPatientDto).toList()
                : all.stream().map(appointmentMapper::toDoctorDto).toList();
    }

    @Override
    @Transactional
    public void cancel(HashId id) {
        Appointment appointment = getById(id);
        if (!appointment.getStatus().equals(AppointmentStatus.SCHEDULED)) {
            throw new AppointmentCancellationConflictException("Appointment can not be cancelled");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);

        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(appointment);
        eventPublisher.publishEvent(new SendEmailEvent<>(appointment.getPatient().getEmail(),
                EmailType.APPOINTMENT_CANCELLATION, emailDto));
        eventPublisher.publishEvent(new SendEmailEvent<>(appointment.getDoctor().getEmail(),
                EmailType.APPOINTMENT_CANCELLATION, emailDto));
    }

    @Override
    @Transactional
    public HashId create(HashId patientId, AppointmentCreateDto dto) {
        List<LocalTime> availableTimes = getAvailableTimes(dto.doctorId(), dto.date());
        if (availableTimes.isEmpty() || !availableTimes.contains(dto.startTime())) {
            throw new IllegalStateException("Selected startTime slot is not available");
        }

        Doctor doctor = doctorService.getById(dto.doctorId());
        Patient patient = patientService.getById(patientId);

        Appointment entity = appointmentMapper.toEntity(dto, doctor, patient);

        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(entity);
        eventPublisher.publishEvent(new SendEmailEvent<>(entity.getPatient().getEmail(),
                EmailType.APPOINTMENT_SCHEDULED, emailDto));

        Appointment saved = appointmentRepository.save(entity);
        return HashId.of(saved.getId());
    }

    @Override
    public List<LocalTime> getAvailableTimes(HashId doctorId, LocalDate date) {
        Doctor doctor = doctorService.getById(doctorId);
        Workday workday = validateWorkday(date);

        Consultation consultation = doctor.getConsultations()
                .stream()
                .filter(c -> c.getWorkday().equals(workday))
                .findFirst()
                .orElseThrow(() -> new DoctorNotAvailableException("Doctor is not available"));

        LocalTime startTime = consultation.getStartTime();
        LocalTime endTime = consultation.getEndTime();

        List<Appointment> appointments = appointmentRepository
                .getScheduledAppointments(doctorId.value(), date);

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime currentSlotStart = startTime;
        int slotDuration = schedulingProperties.getSlotDurationMinutes();

        while (currentSlotStart.isBefore(endTime)) {
            LocalTime currentSlotEnd = currentSlotStart.plusMinutes(slotDuration);

            if (currentSlotEnd.isAfter(endTime)) {
                break;
            }

            boolean isOverlapping = isSlotOccupied(currentSlotStart, currentSlotEnd, appointments);

            if (!isOverlapping) {
                availableSlots.add(currentSlotStart);
            }

            currentSlotStart = currentSlotEnd;
        }

        return availableSlots;
    }

    @Override
    public Appointment getById(HashId id) {
        return appointmentRepository.findById(id.value()).orElseThrow(
                () -> new EntityNotFoundException("Appointment not found"));
    }

    @Override
    public long getTotalAppointmentsToday() {
        return appointmentRepository.countByDate(LocalDate.now());
    }

    @Override
    public long getTotalAppointmentsTodayByDoctorId(HashId id) {
        doctorService.checkExistsById(id);
        return appointmentRepository.countByDateAndDoctorId(LocalDate.now(), id.value());
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatistics() {
        return appointmentRepository.getMonthlyStatistics(LocalDate.now().getYear());
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(HashId id) {
        doctorService.checkExistsById(id);
        return appointmentRepository.getMonthlyStatisticsByDoctorId(id.value(), LocalDate.now().getYear());
    }

    @Override
    public long getDistinctPatientsByDoctorId(HashId doctorId) {
        doctorService.checkExistsById(doctorId);
        return appointmentRepository.countDistinctPatientsByDoctorId(doctorId.value());
    }

    @Override
    public List<Appointment> getAllAppointmentByStatusAndDate(AppointmentStatus status, LocalDate date) {
        return appointmentRepository.findAllByStatusAndDate(status, date);
    }

    @Override
    @Transactional
    public void sendReminderAboutAppointmentsBetween(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("To date must be after from date");
        }

        List<Appointment> appointments = appointmentRepository.getAppointmentsBetween(from, to);
        if (appointments.isEmpty()) {
            return;
        }

        for (Appointment app : appointments) {
            AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(app);
            eventPublisher.publishEvent(new SendEmailEvent<>(
                    app.getPatient().getEmail(),
                    EmailType.UPCOMING_REMINDER,
                    emailDto
            ));
            app.setReminderSent(true);
        }
    }

    @Override
    public List<HashId> findIdsForCancellation(HashId doctorId, Workday dayOfWeek, LocalTime start, LocalTime end) {
        List<Long> rawIds = appointmentRepository.findIdsForCancellation(doctorId.value(), dayOfWeek.name(),
                start, end);

        return rawIds.stream().map(HashId::of).toList();
    }

    private Workday validateWorkday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new DoctorNotAvailableException("Doctor is not available on weekends");
        }
        return Workday.valueOf(date.getDayOfWeek().name());
    }

    private boolean isSlotOccupied(LocalTime slotStart, LocalTime slotEnd, List<Appointment> appointments) {
        for (Appointment app : appointments) {
            if (slotStart.isBefore(app.getEndTime()) && slotEnd.isAfter(app.getStartTime())) {
                return true;
            }
        }
        return false;
    }
}
