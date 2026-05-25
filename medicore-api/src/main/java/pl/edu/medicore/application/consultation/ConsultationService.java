package pl.edu.medicore.application.consultation;

import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ConsultationService {
    List<ConsultationDto> findByDoctorId(UUID doctorId);

    UUID create(ConsultationCreateDto dto);

    UUID update(UUID id, ConsultationUpdateDto dto);

    void delete(UUID id);
}
