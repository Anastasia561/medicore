package pl.edu.medicore.application.consultation;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.appointment.AppointmentService;
import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.email.dto.ScheduleEmailDto;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.application.email.EmailType;
import pl.edu.medicore.common.config.properties.ConsultationProperties;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper consultationMapper;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final ConsultationProperties consultationProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ConsultationDto> findByDoctorId(UUID doctorId) {
        return doctorService.getByPublicId(doctorId).getConsultations()
                .stream()
                .sorted(Comparator.comparing(Consultation::getWorkday))
                .map(consultationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UUID create(ConsultationCreateDto dto) {
        Doctor doctor = doctorService.getByPublicId(dto.doctorId());

        checkExistsByDay(doctor, dto.day());
        validateTime(dto.startTime(), dto.endTime());

        Consultation consultation = consultationMapper.toEntity(dto, doctor);

        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation);
        eventPublisher.publishEvent(new SendEmailEvent<>(consultation.getDoctor().getEmail(), EmailType.SCHEDULE_UPDATE, emailDto));
        return consultationRepository.save(consultation).getPublicId();
    }

    @Override
    @Transactional
    public UUID update(UUID id, ConsultationUpdateDto dto) {
        Consultation consultation = consultationRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));

        LocalTime oldStart = consultation.getStartTime();
        LocalTime oldEnd = consultation.getEndTime();

        validateTime(dto.startTime(), dto.endTime());
        consultationMapper.updateConsultationFromDto(dto, consultation);

        if (dto.startTime().isAfter(oldStart)) {
            cancelAppointmentsInRange(consultation, oldStart, dto.startTime());
        }

        if (dto.endTime().isBefore(oldEnd)) {
            cancelAppointmentsInRange(consultation, dto.endTime(), oldEnd);
        }

        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation);
        eventPublisher.publishEvent(new SendEmailEvent<>(
                consultation.getDoctor().getEmail(),
                EmailType.SCHEDULE_UPDATE,
                emailDto
        ));

        return id;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Optional<Consultation> consultation = consultationRepository.findByPublicId(id);
        if (consultation.isEmpty()) {
            throw new EntityNotFoundException("Consultation not found");
        }
        Consultation entity = consultation.get();

        cancelAppointmentsInRange(entity, entity.getStartTime(), entity.getEndTime());

        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation.get());
        eventPublisher.publishEvent(new SendEmailEvent<>(consultation.get().getDoctor().getEmail(), EmailType.SCHEDULE_UPDATE, emailDto));
        consultationRepository.deleteByPublicId(id);
    }

    private void cancelAppointmentsInRange(Consultation consultation, LocalTime start, LocalTime end) {
        appointmentService.findIdsForCancellation(
                consultation.getDoctor().getId(),
                consultation.getWorkday(),
                start,
                end
        ).forEach(appointmentService::cancel);
    }

    private void checkExistsByDay(Doctor doctor, Workday workday) {
        boolean exists = doctor
                .getConsultations()
                .stream()
                .anyMatch(c -> c.getWorkday().equals(workday));

        if (exists) {
            throw new EntityExistsException("Doctor has consultation schedule for selected day");
        }
    }

    private void validateTime(LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        if (end.isAfter(consultationProperties.getEnd())) {
            throw new IllegalArgumentException("End time must be in valid range");
        }

        if (start.isBefore(consultationProperties.getStart())) {
            throw new IllegalArgumentException("Start time must be in valid range");
        }
    }
}
