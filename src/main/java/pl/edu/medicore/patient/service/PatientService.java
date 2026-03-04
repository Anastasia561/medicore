package pl.edu.medicore.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Status;

public interface PatientService {
    Page<PatientResponseDto> findAll(String search, Pageable pageable);

    Patient getById(Long id);

    long register(PatientRegisterDto dto);

    void updateStatus(String email, Status status);
}
