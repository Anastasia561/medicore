package pl.edu.medicore.doctor.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.mapper.DoctorMapper;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.doctor.repository.DoctorRepository;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;
import pl.edu.medicore.utils.UrlBuilder;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.service.VerificationTokenService;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private VerificationTokenService verificationTokenService;
    @Mock
    private UrlBuilder urlBuilder;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private DoctorServiceImpl doctorService;


    @Test
    void shouldNotThrowEntityNotFoundException_whenDoctorExistsWithCheckById() {
        Long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);

        assertDoesNotThrow(() -> doctorService.checkExistsById(doctorId));

        verify(doctorRepository).existsById(doctorId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistWithCheckById() {
        Long doctorId = 1L;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.checkExistsById(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorRepository).existsById(doctorId);
    }

    @Test
    void shouldReturnDoctorById_whenDoctorExists() {
        Long doctorId = 1L;
        Doctor doctor = new Doctor();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Doctor result = doctorService.getById(doctorId);

        assertEquals(doctor, result);
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenDoctorDoesNotExistById() {
        Long doctorId = 1L;

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> doctorService.getById(doctorId)
        );

        assertEquals("Doctor not found", exception.getMessage());
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void shouldReturnMappedPageOdDoctors_whenFilterIsNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Doctor doctor = new Doctor();
        DoctorResponseDto responseDto = new DoctorResponseDto("John", "Doe", "test@mail.com",
                Specialization.DERMATOLOGIST, 10, LocalDate.of(2023, 10, 10));

        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

        when(doctorRepository.findAll(pageable)).thenReturn(doctorPage);
        when(doctorMapper.toDoctorResponseDto(doctor)).thenReturn(responseDto);

        Page<DoctorResponseDto> result = doctorService.getAll(null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(responseDto, result.getContent().getFirst());

        verify(doctorRepository).findAll(pageable);
        verify(doctorMapper).toDoctorResponseDto(doctor);
        verify(doctorRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void shouldReturnMappedPageOfDoctors_whenFilterIsProvided() {
        DoctorFilterDto filter = new DoctorFilterDto("", Specialization.DERMATOLOGIST);
        Pageable pageable = PageRequest.of(0, 10);

        Doctor doctor = new Doctor();
        DoctorResponseDto responseDto = new DoctorResponseDto("John", "Doe", "test@mail.com",
                Specialization.DERMATOLOGIST, 10, LocalDate.of(2023, 10, 10));

        Page<Doctor> doctorPage = new PageImpl<>(List.of(doctor));

        when(doctorRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(doctorPage);
        when(doctorMapper.toDoctorResponseDto(doctor)).thenReturn(responseDto);

        Page<DoctorResponseDto> result = doctorService.getAll(filter, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(responseDto, result.getContent().getFirst());

        verify(doctorRepository).findAll(any(Specification.class), eq(pageable));
        verify(doctorMapper).toDoctorResponseDto(doctor);
        verify(doctorRepository, never()).findAll(pageable);
    }

    @Test
    void shouldReturnDoctorBySpecializationStatistics_whenDoctorsExist() {
        List<DoctorStatisticsDto> expected = List.of(
                new DoctorStatisticsDto(Specialization.CARDIOLOGIST, 5L),
                new DoctorStatisticsDto(Specialization.NEUROLOGIST, 3L)
        );

        when(doctorRepository.countDoctorsBySpecialization()).thenReturn(expected);

        List<DoctorStatisticsDto> result = doctorService.getDoctorBySpecialization();

        assertEquals(expected, result);
        verify(doctorRepository).countDoctorsBySpecialization();
    }

    @Test
    void shouldGetTotalCount_whenDoctorsExist() {
        when(doctorRepository.count()).thenReturn(15L);

        long result = doctorService.getTotalCount();

        assertEquals(15L, result);
        verify(doctorRepository).count();
    }

    @Test
    void shouldCreateTokenBuildLinkAndPublishEmailEvent_whenInvitingNewDoctor() {
        DoctorInvitationRequestDto dto = new DoctorInvitationRequestDto("test@gmail.com", "John",
                "Smith");

        String token = "generated-token";
        String link = "http://localhost:8080/register?token=generated-token";

        when(verificationTokenService.createToken(dto.email(), TokenType.DOCTOR_INVITATION, Duration.ofDays(10)
        )).thenReturn(token);

        when(urlBuilder.buildDoctorRegistrationUrl(token)).thenReturn(link);

        doctorService.invite(dto);

        verify(verificationTokenService).createToken("test@gmail.com", TokenType.DOCTOR_INVITATION,
                Duration.ofDays(10));

        verify(urlBuilder).buildDoctorRegistrationUrl(token);
    }

    @Test
    void shouldRegisterDoctor_whenInputIsValid() {
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token123", "test@gmail.com",
                "John", "Doe", "password123", "password123",
                Gender.MALE, 10, Specialization.DERMATOLOGIST);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        ConfirmationEmailDto emailDto = new ConfirmationEmailDto("John", "Smith");

        when(doctorMapper.toEntity(dto)).thenReturn(doctor);
        when(doctorMapper.toEmailDto(doctor)).thenReturn(emailDto);
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        long result = doctorService.register(dto);

        assertEquals(1L, result);

        verify(verificationTokenService).validateToken("token123", TokenType.DOCTOR_INVITATION,
                "test@gmail.com");

        verify(doctorMapper).toEntity(dto);
        verify(doctorMapper).toEmailDto(doctor);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenPasswordsDoNotMatch() {
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token123", "test@gmail.com",
                "John", "Doe", "password123", "password",
                Gender.MALE, 10, Specialization.DERMATOLOGIST);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.register(dto)
        );

        assertEquals("Passwords don't match", exception.getMessage());

        verify(verificationTokenService).validateToken("token123", TokenType.DOCTOR_INVITATION,
                "test@gmail.com");

        verifyNoInteractions(doctorMapper, doctorRepository, eventPublisher);
    }
}
