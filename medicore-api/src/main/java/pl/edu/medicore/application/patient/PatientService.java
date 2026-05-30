package pl.edu.medicore.application.patient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.person.Status;
import pl.edu.medicore.common.encryption.HashId;

import java.util.UUID;

public interface PatientService {
    Page<PatientResponseDto> findAll(String search, Pageable pageable);

    Patient getById(HashId id);

    void checkExistsById(HashId id);

    long register(PatientRegisterDto dto);

    void updateStatus(String email, Status status);

    long getTotalCount();
}
