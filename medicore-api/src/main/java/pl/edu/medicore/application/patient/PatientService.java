package pl.edu.medicore.application.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.patient.dto.PatientMedicalProfileDto;
import pl.edu.medicore.application.patient.dto.PatientMedicalProfileUpdateDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.person.UserStatus;
import pl.edu.medicore.common.encryption.HashId;

public interface PatientService {
    Page<PatientResponseDto> findAll(String search, Pageable pageable);

    Patient getById(HashId id);

    void checkExistsById(HashId id);

    PatientMedicalProfileDto getMedicalProfile(HashId id);

    HashId updateMedicalProfile(HashId id, PatientMedicalProfileUpdateDto dto);

    long register(PatientRegisterDto dto);

    void updateStatus(String email, UserStatus status);

    long getTotalCount();
}
