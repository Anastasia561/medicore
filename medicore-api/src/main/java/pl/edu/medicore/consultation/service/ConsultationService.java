package pl.edu.medicore.consultation.service;

import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.model.Consultation;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ConsultationService {
    List<ConsultationDto> findByDoctorId(UUID doctorId);

    UUID create(ConsultationCreateDto dto);

    UUID update(UUID id, ConsultationUpdateDto dto);

    void delete(UUID id);

    Consultation findByDoctorIdAndDate(UUID doctorId, LocalDate date);
}
