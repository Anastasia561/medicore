package pl.edu.medicore.application.doctor;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.application.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.application.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.application.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.application.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.email.dto.VerificationEmailDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.application.email.EmailType;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.infrastructure.storage.UrlBuilder;
import pl.edu.medicore.application.verification.TokenType;
import pl.edu.medicore.application.verification.VerificationTokenService;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final VerificationTokenService verificationTokenService;
    private final UrlBuilder urlBuilder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void checkExistsById(HashId doctorId) {
        if (!doctorRepository.existsById(doctorId.value())) {
            throw new EntityNotFoundException("Doctor not found");
        }
    }

    @Override
    public Doctor getById(HashId doctorId) {
        return doctorRepository.findById(doctorId.value()).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found"));
    }

    @Override
    public Page<DoctorResponseDto> getAll(DoctorFilterDto filter, Pageable pageable) {
        Page<Doctor> all = (filter == null) ? doctorRepository.findAll(pageable)
                : doctorRepository.findAll(DoctorSpecification.search(filter), pageable);

        return all.map(doctorMapper::toDoctorResponseDto);
    }

    @Override
    public List<DoctorStatisticsDto> getDoctorBySpecialization() {
        return doctorRepository.countDoctorsBySpecialization();
    }

    @Override
    public long getTotalCount() {
        return doctorRepository.count();
    }

    @Override
    public void invite(DoctorInvitationRequestDto dto) {
        String token = verificationTokenService.createToken(dto.email(), TokenType.DOCTOR_INVITATION,
                Duration.ofDays(10));

        String link = urlBuilder.buildDoctorRegistrationUrl(token);
        VerificationEmailDto emailDto = new VerificationEmailDto(dto.firstName(), dto.lastName(), link);
        eventPublisher.publishEvent(new SendEmailEvent<>(dto.email(), EmailType.DOCTOR_INVITE, emailDto));
    }

    @Override
    @Transactional
    public long register(DoctorRegistrationDto dto) {
        verificationTokenService.validateToken(dto.token(), TokenType.DOCTOR_INVITATION, dto.email());

        if (!dto.password().equals(dto.repeatPassword()))
            throw new IllegalArgumentException("Passwords don't match");

        Doctor entity = doctorMapper.toEntity(dto);

        ConfirmationEmailDto emailDto = doctorMapper.toEmailDto(entity);
        eventPublisher.publishEvent(new SendEmailEvent<>(dto.email(), EmailType.REGISTRATION_CONFIRMATION, emailDto));
        return doctorRepository.save(entity).getId();
    }
}
