package pl.edu.medicore.doctor.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.mapper.DoctorMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.repository.DoctorRepository;
import pl.edu.medicore.doctor.repository.specification.DoctorSpecification;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.service.VerificationTokenService;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final VerificationTokenService verificationTokenService;

    @Override
    public void checkExistsById(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new EntityNotFoundException("Doctor not found");
        }
    }

    @Override
    public Doctor getById(Long id) {
        return doctorRepository.findById(id).orElseThrow(
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
    public void invite(String email) {
        String token = verificationTokenService.createToken(email, TokenType.DOCTOR_INVITATION,
                Duration.ofDays(10));
        //send via email
        String link = "https://medicore.com/register?token=" + token;
        System.out.println("Link: " + link);
    }

    @Override
    @Transactional
    public long register(DoctorRegistrationDto dto) {
        verificationTokenService.validateToken(dto.token(), TokenType.DOCTOR_INVITATION, dto.email());

        if (!dto.password().equals(dto.repeatPassword()))
            throw new IllegalArgumentException("Passwords don't match");

        Doctor entity = doctorMapper.toEntity(dto);
        return doctorRepository.save(entity).getId();
    }
}
