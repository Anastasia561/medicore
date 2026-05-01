package pl.edu.medicore.doctor.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.mapper.DoctorMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.repository.DoctorRepository;
import pl.edu.medicore.doctor.repository.specification.DoctorSpecification;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.email.dto.VerificationEmailDto;
import pl.edu.medicore.infrastructure.messaging.event.SendEmailEvent;
import pl.edu.medicore.email.model.EmailType;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.infrastructure.storage.UrlBuilder;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.service.VerificationTokenService;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final VerificationTokenService verificationTokenService;
    private final UrlBuilder urlBuilder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void checkExistsById(UUID doctorId) {
        if (!doctorRepository.existsByPublicId(doctorId)) {
            throw new EntityNotFoundException("Doctor not found");
        }
    }

    @Override
    public Doctor getByPublicId(UUID id) {
        return doctorRepository.findByPublicId(id).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found"));
    }

    @Override
    public Doctor getById(long doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(
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
