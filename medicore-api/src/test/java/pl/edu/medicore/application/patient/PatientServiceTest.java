package pl.edu.medicore.application.patient;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.address.Address;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.UserStatus;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.storage.UrlBuilder;
import pl.edu.medicore.application.verification.VerificationTokenService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VerificationTokenService verificationTokenService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private UrlBuilder urlBuilder;
    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void shouldReturnAllPatients_whenQueryIsNull() {
        Patient patient = new Patient();
        PatientResponseDto dto = new PatientResponseDto(HashId.of(1L), "John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new AddressDto("Poland", "Warsaw", "Street", "10"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(patient));

        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        when(patientMapper.toPatientResponseDto(patient)).thenReturn(dto);

        Page<PatientResponseDto> result = patientService.findAll(null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(patientRepository).findAll(pageable);
        verify(patientMapper).toPatientResponseDto(patient);
    }

    @Test
    void shouldReturnAllPatients_whenQueryIsBlank() {
        Patient patient = new Patient();
        PatientResponseDto dto = new PatientResponseDto(HashId.of(1L), "John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new AddressDto("Poland", "Warsaw", "Street", "10"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(patient));

        when(patientRepository.findAll(pageable)).thenReturn(patientPage);
        when(patientMapper.toPatientResponseDto(patient)).thenReturn(dto);

        Page<PatientResponseDto> result = patientService.findAll("", pageable);

        assertEquals(1, result.getTotalElements());
        verify(patientRepository).findAll(pageable);
        verify(patientMapper).toPatientResponseDto(patient);
    }

    @Test
    void shouldSearchPatients_whenQueryIsProvided() {
        String query = "john";
        Patient patient = new Patient();
        PatientResponseDto dto = new PatientResponseDto(HashId.of(1L), "John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new AddressDto("Poland", "Warsaw", "Street", "10"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(patient));

        when(patientRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(patientPage);
        when(patientMapper.toPatientResponseDto(patient)).thenReturn(dto);

        Page<PatientResponseDto> result = patientService.findAll(query, pageable);

        assertEquals(1, result.getTotalElements());
        verify(patientRepository).findAll(any(Specification.class), eq(pageable));
        verify(patientMapper).toPatientResponseDto(patient);
    }

    @Test
    void shouldReturnPatient_whenPatientExists() {
        long patientId = 1L;
        HashId hashId = HashId.of(patientId);
        Patient patient = new Patient();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Patient result = patientService.getById(hashId);

        assertNotNull(result);
        assertEquals(patient, result);
        verify(patientRepository).findById(patientId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPatientNotFound() {
        long patientId = 1L;
        HashId hashId = HashId.of(patientId);

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.getById(hashId)
        );

        assertEquals("Patient not found", ex.getMessage());
        verify(patientRepository).findById(patientId);
    }

    @Test
    void shouldNotThrowEntityNotFoundException_whenPatientExistsById() {
        long patientId = 1L;
        HashId hashId = HashId.of(patientId);

        when(patientRepository.existsById(patientId)).thenReturn(true);

        patientService.checkExistsById(hashId);

        verify(patientRepository).existsById(patientId);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPatientNotFoundByCheckById() {
        long patientId = 1L;
        HashId hashId = HashId.of(patientId);

        when(patientRepository.existsById(patientId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.checkExistsById(hashId)
        );

        assertEquals("Patient not found", ex.getMessage());
        verify(patientRepository).existsById(patientId);
    }

    @Test
    void shouldRegisterPatient_whenInputIsValid() {
        AddressDto addressDto = new AddressDto("Poland", "Warsaw", "Street", "10");
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass", "pass", Gender.MALE, 67.8, 167.8,
                PregnancyStatus.NOT_APPLICABLE, LocalDate.of(2006, 7, 2), "123", addressDto);

        Address address = new Address();
        Patient patient = new Patient();
        Patient savedPatient = new Patient();
        savedPatient.setId(1L);

        when(addressMapper.toEntity(addressDto)).thenReturn(address);
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(verificationTokenService.createToken(any(), any(), any())).thenReturn("token");
        when(urlBuilder.buildEmailVerificationUrl("token")).thenReturn("http://link");
        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        long result = patientService.register(dto);


        assertEquals(1L, result);
        assertEquals(address, patient.getAddress());
        assertEquals("encodedPass", patient.getPassword());
        assertEquals("test@gmail.com", patient.getEmail());

        verify(patientRepository).save(patient);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenPasswordsDoNotMatch() {
        AddressDto addressDto = new AddressDto("Poland", "Warsaw", "Street", "10");
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass1", "pass2", Gender.MALE, 67.8, 167.9,
                PregnancyStatus.NOT_APPLICABLE, LocalDate.of(2006, 7, 2),
                "123", addressDto);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> patientService.register(dto));

        assertEquals("Passwords don't match", ex.getMessage());

        verifyNoInteractions(patientRepository);
        verifyNoInteractions(applicationEventPublisher);
    }

    @Test
    void shouldNotSendEmail_whenSavingPatientFails() {
        AddressDto addressDto = new AddressDto("Poland", "Warsaw", "Street", "10");
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass", "pass", Gender.MALE, 67.8, 167.8,
                PregnancyStatus.NOT_APPLICABLE, LocalDate.of(2006, 7, 2),
                "123", addressDto);

        Address address = new Address();
        Patient patient = new Patient();

        when(addressMapper.toEntity(addressDto)).thenReturn(address);
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(verificationTokenService.createToken(any(), any(), any())).thenReturn("token");
        when(urlBuilder.buildEmailVerificationUrl("token")).thenReturn("http://link");
        when(patientRepository.save(any(Patient.class))).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> patientService.register(dto));
    }

    @Test
    void shouldUpdateStatusAndSendEmail_whenPatientExists() {
        String email = "test@mail.com";
        UserStatus status = UserStatus.ACTIVE;

        Patient patient = new Patient();
        patient.setEmail(email);

        ConfirmationEmailDto emailDto = new ConfirmationEmailDto("John", "Doe");

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(patientMapper.toEmailDto(patient)).thenReturn(emailDto);

        patientService.updateStatus(email, status);

        assertEquals(status, patient.getStatus());
        verify(patientRepository).findByEmail(email);
        verify(patientMapper).toEmailDto(patient);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPatientNotFoundForStatusUpdate() {
        String email = "test@gmail.com";

        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.updateStatus(email, UserStatus.ACTIVE)
        );

        assertEquals("Patient not found", ex.getMessage());
        verify(patientRepository).findByEmail(email);
    }

    @Test
    void shouldReturnTotalPatientCount_whenPatientsExist() {
        when(patientRepository.count()).thenReturn(2L);

        assertEquals(2L, patientRepository.count());
    }
}
