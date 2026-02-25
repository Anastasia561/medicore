package pl.edu.medicore.consultation.service;

import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.model.Consultation;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {
    List<ConsultationDto> findByDoctorId(Long doctorId);

    long create(ConsultationCreateDto dto);

    long update(Long id, ConsultationUpdateDto dto);

    void delete(Long id);

    Consultation findByDoctorIdAndDate(Long doctorId, LocalDate date);
}
