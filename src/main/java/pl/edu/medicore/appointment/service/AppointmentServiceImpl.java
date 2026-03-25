package pl.edu.medicore.appointment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.mapper.AppointmentMapper;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.appointment.repository.AppointmentRepository;
import pl.edu.medicore.appointment.repository.specification.AppointmentSpecification;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.service.ConsultationService;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.email.model.EmailType;
import pl.edu.medicore.email.service.EmailService;
import pl.edu.medicore.exception.AppointmentAlreadyCancelledException;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.config.properties.SchedulingProperties;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
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
    private final ConsultationService consultationService;
    private final EmailService emailService;

    @Override
    public Page<AppointmentInfoDto> getAppointmentsInRange(AppointmentFilterDto filter, Pageable pageable) {
        Role role = personService.getRoleById(filter.userId());

        if (filter.endDate().isBefore(filter.startDate()))
            throw new IllegalArgumentException("End date must be after start date");

        Page<Appointment> all = appointmentRepository
                .findAll(AppointmentSpecification.withFilter(filter), pageable);

        return role == Role.DOCTOR ? all.map(appointmentMapper::toPatientDto)
                : all.map(appointmentMapper::toDoctorDto);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Appointment appointment = getById(id);
        if (appointment.getStatus().equals(Status.CANCELLED)) {
            throw new AppointmentAlreadyCancelledException("Appointment is already cancelled");
        }
        appointment.setStatus(Status.CANCELLED);

        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(appointment);
        emailService.sendEmail(appointment.getPatient().getEmail(), EmailType.APPOINTMENT_CANCELLATION, emailDto);
        emailService.sendEmail(appointment.getDoctor().getEmail(), EmailType.APPOINTMENT_CANCELLATION, emailDto);
    }

    @Override
    @Transactional
    public long create(Long patientId, AppointmentCreateDto dto) {
        List<LocalTime> availableTimes = getAvailableTimes(dto.doctorId(), dto.date());
        if (availableTimes.isEmpty() || !availableTimes.contains(dto.time())) {
            throw new IllegalStateException("Selected time slot is not available");
        }

        Doctor doctor = doctorService.getById(dto.doctorId());
        Patient patient = patientService.getById(patientId);

        Appointment entity = appointmentMapper.toEntity(dto, doctor, patient);
        AppointmentNotificationEmailDto emailDto = appointmentMapper.toEmailDto(entity);
        emailService.sendEmail(entity.getPatient().getEmail(), EmailType.APPOINTMENT_SCHEDULED, emailDto);

        return appointmentRepository.save(entity).getId();
    }

    @Override
    public List<LocalTime> getAvailableTimes(Long doctorId, LocalDate date) {
        Consultation consultation = consultationService.findByDoctorIdAndDate(doctorId, date);
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
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Appointment not found"));
    }

    @Override
    public long getTotalAppointmentsToday() {
        return appointmentRepository.countByDate(LocalDate.now());
    }

    @Override
    public long getTotalAppointmentsTodayByDoctorId(long id) {
        return appointmentRepository.countByDateAndDoctorId(LocalDate.now(), id);
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatistics() {
        return appointmentRepository.getMonthlyStatistics(LocalDate.now().getYear());
    }

    @Override
    public List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(long id) {
        return appointmentRepository.getMonthlyStatisticsByDoctorId(id, LocalDate.now().getYear());
    }

    @Override
    public long getDistinctPatientsByDoctorId(long doctorId) {
        return appointmentRepository.countDistinctPatientsByDoctorId(doctorId);
    }
}
