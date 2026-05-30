package pl.edu.medicore.application.consultation;

import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

public interface ConsultationService {
    List<ConsultationDto> findByDoctorId(HashId doctorId);

    HashId create(ConsultationCreateDto dto);

    HashId update(HashId id, ConsultationUpdateDto dto);

    void delete(HashId id);
}
