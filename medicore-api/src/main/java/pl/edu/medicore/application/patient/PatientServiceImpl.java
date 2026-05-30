package pl.edu.medicore.application.patient;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.address.Address;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.email.dto.VerificationEmailDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.application.email.EmailType;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.person.Status;
import pl.edu.medicore.infrastructure.storage.UrlBuilder;
import pl.edu.medicore.application.verification.TokenType;
import pl.edu.medicore.application.verification.VerificationTokenService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AddressMapper addressMapper;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final UrlBuilder urlBuilder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<PatientResponseDto> findAll(String query, Pageable pageable) {
        Page<Patient> all = (query == null || query.isBlank()) ? patientRepository.findAll(pageable)
                : patientRepository.findAll(PatientSpecification.search(query), pageable);

        return all.map(patientMapper::toPatientResponseDto);
    }

    @Override
    public Patient getById(HashId id) {
        return patientRepository.findById(id.value())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    @Override
    public void checkExistsById(HashId id) {
        if (!patientRepository.existsById(id.value())) {
            throw new EntityNotFoundException("Patient not found");
        }
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
        String token = verificationTokenService.createToken(dto.email(), TokenType.EMAIL_VERIFICATION,
                Duration.ofMinutes(5));

        String link = urlBuilder.buildEmailVerificationUrl(token);
        VerificationEmailDto emailDto = new VerificationEmailDto(dto.firstName(), dto.lastName(), link);
        Patient saved = patientRepository.save(patient);
        eventPublisher.publishEvent(new SendEmailEvent<>(dto.email(), EmailType.EMAIL_VERIFICATION, emailDto));
        return saved.getId();
    }

    @Override
    @Transactional
    public void updateStatus(String email, Status status) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        patient.setStatus(status);

        ConfirmationEmailDto emailDto = patientMapper.toEmailDto(patient);
        eventPublisher.publishEvent(new SendEmailEvent<>(email, EmailType.REGISTRATION_CONFIRMATION, emailDto));
    }

    @Override
    public long getTotalCount() {
        return patientRepository.count();
    }
}
