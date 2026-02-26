package pl.edu.medicore.patient.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.mapper.PatientMapper;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.repository.PatientRepository;
import pl.edu.medicore.patient.repository.specification.PatientSpecification;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public Page<PatientResponseDto> findAll(String query, Pageable pageable) {
        Page<Patient> all = (query == null || query.isBlank()) ? patientRepository.findAll(pageable)
                : patientRepository.findAll(PatientSpecification.search(query), pageable);

        return all.map(patientMapper::toPatientResponseDto);
    }

    @Override
    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }
}
