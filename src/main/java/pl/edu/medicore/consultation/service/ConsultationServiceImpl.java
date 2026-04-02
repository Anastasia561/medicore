package pl.edu.medicore.consultation.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pl.edu.medicore.email.event.SendEmailEvent;
import pl.edu.medicore.email.model.EmailType;
import pl.edu.medicore.email.service.EmailService;
import pl.edu.medicore.exception.DoctorNotAvailableException;
import pl.edu.medicore.config.properties.ConsultationProperties;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper consultationMapper;
    private final DoctorService doctorService;
    private final ConsultationProperties consultationProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ConsultationDto> findByDoctorId(Long doctorId) {
        doctorService.checkExistsById(doctorId);

        return consultationRepository.findByDoctorId(doctorId)
                .stream().map(consultationMapper::toDto)
                .toList();
    }

    @Override
    public Consultation findByDoctorIdAndDate(Long doctorId, LocalDate date) {
        doctorService.checkExistsById(doctorId);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new DoctorNotAvailableException("Doctor is not available on weekends");
        }
        Workday workday = Workday.valueOf(date.getDayOfWeek().name());

        return consultationRepository.findByDoctorIdAndWorkday(doctorId, workday)
                .orElseThrow(() -> new DoctorNotAvailableException("Doctor is not available"));
    }

    @Override
    @Transactional
    public long create(ConsultationCreateDto dto) {
        checkExistsByDay(dto.doctorId(), dto.day());
        validateTime(dto.startTime(), dto.endTime());

        Doctor doctor = doctorService.getById(dto.doctorId());

        Consultation consultation = consultationMapper.toEntity(dto, doctor);

        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation);
        eventPublisher.publishEvent(new SendEmailEvent<>(consultation.getDoctor().getEmail(), EmailType.SCHEDULE_UPDATE, emailDto));
        return consultationRepository.save(consultation).getId();
    }

    @Override
    @Transactional
    public long update(Long id, ConsultationUpdateDto dto) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        validateTime(dto.startTime(), dto.endTime());
        consultationMapper.updateConsultationFromDto(dto, consultation);

        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation);
        eventPublisher.publishEvent(new SendEmailEvent<>(consultation.getDoctor().getEmail(), EmailType.SCHEDULE_UPDATE, emailDto));
        return id;
    }

    @Override
    public void delete(Long id) {
        Optional<Consultation> consultation = consultationRepository.findById(id);
        if (consultation.isEmpty()) {
            throw new EntityNotFoundException("Consultation not found");
        }
        ScheduleEmailDto emailDto = consultationMapper.toEmailDto(consultation.get());
        eventPublisher.publishEvent(new SendEmailEvent<>(consultation.get().getDoctor().getEmail(), EmailType.SCHEDULE_UPDATE, emailDto));
        consultationRepository.deleteById(id);
    }

    private void checkExistsByDay(Long doctorId, Workday workday) {
        if (consultationRepository.existsByDoctorIdAndWorkday(doctorId, workday)) {
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
