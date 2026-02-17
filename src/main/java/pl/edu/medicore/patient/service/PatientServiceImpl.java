package pl.edu.medicore.patient.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.mapper.PatientMapper;
import pl.edu.medicore.patient.repository.PatientRepository;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public Page<PatientResponseDto> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::patientToPatientResponseDto);
    }
}
