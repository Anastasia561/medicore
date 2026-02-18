package pl.edu.medicore.consultation.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
import pl.edu.medicore.properties.ConsultationProperties;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper consultationMapper;
    private final DoctorService doctorService;
    private final ConsultationProperties consultationProperties;

    @Override
    public List<ConsultationDto> findByDoctorId(Long doctorId) {
        doctorService.checkExistsById(doctorId);
        return consultationRepository.findByDoctorId(doctorId)
                .stream().map(consultationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public long create(ConsultationCreateDto dto) {
        checkExistsByDay(dto.doctorId(), dto.day());
        validateTime(dto.startTime(), dto.endTime());

        Doctor doctor = doctorService.getById(dto.doctorId());
        Consultation consultation = consultationMapper.toEntity(dto, doctor);
        return consultationRepository.save(consultation).getId();
    }

    @Override
    @Transactional
    public long update(Long id, ConsultationUpdateDto dto) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation not found"));
        validateTime(dto.startTime(), dto.endTime());
        consultationMapper.updateConsultationFromDto(dto, consultation);
        return id;
    }

    @Override
    public void delete(Long id) {
        if (consultationRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Consultation not found");
        }
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
