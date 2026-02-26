package pl.edu.medicore.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.model.Patient;

public interface PatientService {
    Page<PatientResponseDto> findAll(String search, Pageable pageable);

    Patient getById(Long id);
}
