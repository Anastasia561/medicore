package pl.edu.medicore.patient.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.address.model.Address;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.mapper.PatientMapper;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.repository.PatientRepository;
import pl.edu.medicore.patient.repository.specification.PatientSpecification;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.service.VerificationTokenService;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

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

    @Override
    @Transactional
    public long register(PatientRegisterDto dto) {
        if (!dto.password().equals(dto.repeatPassword()))
            throw new IllegalArgumentException("Passwords don't match");

        Address address = addressMapper.toEntity(dto.address());
        Patient patient = patientMapper.toEntity(dto);

        patient.setAddress(address);
        patient.setPassword(passwordEncoder.encode(dto.password()));
        patient.setEmail(dto.email().toLowerCase());
        String token = verificationTokenService.createToken(dto.email(), TokenType.EMAIL_VERIFICATION);
        //send via email
        System.out.println("Token: " + token);
        return patientRepository.save(patient).getId();
    }

    @Override
    @Transactional
    public void updateStatus(String email, Status status) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        patient.setStatus(status);
    }

    @Override
    public long getTotalCount() {
        return patientRepository.count();
    }
}
