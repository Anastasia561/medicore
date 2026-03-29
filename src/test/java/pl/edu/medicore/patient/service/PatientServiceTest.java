package pl.edu.medicore.patient.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.address.model.Address;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.email.dto.VerificationEmailDto;
import pl.edu.medicore.email.model.EmailType;
import pl.edu.medicore.email.service.EmailService;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.mapper.PatientMapper;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.repository.PatientRepository;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.utils.UrlBuilder;
import pl.edu.medicore.verification.service.VerificationTokenService;

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
    private EmailService emailService;
    @Mock
    private UrlBuilder urlBuilder;
    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void shouldReturnAllPatients_whenQueryIsNull() {
        Patient patient = new Patient();
        PatientResponseDto dto = new PatientResponseDto("John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new PatientAddressDto("Poland", "Warsaw", "Street", 10));

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
        PatientResponseDto dto = new PatientResponseDto("John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new PatientAddressDto("Poland", "Warsaw", "Street", 10));

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
        PatientResponseDto dto = new PatientResponseDto("John", "Doe", "test@gmail.com",
                "123", LocalDate.of(1990, 10, 10),
                new PatientAddressDto("Poland", "Warsaw", "Street", 10));

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
        Long id = 1L;
        Patient patient = new Patient();

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        Patient result = patientService.getById(id);

        assertNotNull(result);
        assertEquals(patient, result);
        verify(patientRepository).findById(id);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPatientNotFound() {
        Long id = 1L;

        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.getById(id)
        );

        assertEquals("Patient not found", ex.getMessage());
        verify(patientRepository).findById(id);
    }

    @Test
    void shouldRegisterPatient_whenInputIsValid() {
        PatientAddressDto addressDto = new PatientAddressDto("Poland", "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass", "pass", Gender.MALE, 67.8, 167.8,LocalDate.of(2006, 7, 2), "123", addressDto);

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
        verify(emailService).sendEmail(eq("test@gmail.com"),
                eq(EmailType.EMAIL_VERIFICATION), any(VerificationEmailDto.class));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenPasswordsDoNotMatch() {
        PatientAddressDto addressDto = new PatientAddressDto("Poland", "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass1", "pass2", Gender.MALE, 67.8, 167.9,LocalDate.of(2006, 7, 2),
                "123", addressDto);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> patientService.register(dto));

        assertEquals("Passwords don't match", ex.getMessage());

        verifyNoInteractions(patientRepository);
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmail_whenSavingPatientFails() {
        PatientAddressDto addressDto = new PatientAddressDto("Poland", "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "pass", "pass", Gender.MALE, 67.8, 167.8,LocalDate.of(2006, 7, 2),
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
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldUpdateStatusAndSendEmail_whenPatientExists() {
        String email = "test@mail.com";
        Status status = Status.ACTIVE;

        Patient patient = new Patient();
        patient.setEmail(email);

        ConfirmationEmailDto emailDto = new ConfirmationEmailDto("John", "Doe");

        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(patient));
        when(patientMapper.toEmailDto(patient)).thenReturn(emailDto);

        patientService.updateStatus(email, status);

        assertEquals(status, patient.getStatus());
        verify(patientRepository).findByEmail(email);
        verify(patientMapper).toEmailDto(patient);
        verify(emailService).sendEmail(email, EmailType.REGISTRATION_CONFIRMATION, emailDto);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenPatientNotFoundForStatusUpdate() {
        String email = "test@gmail.com";

        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.updateStatus(email, Status.ACTIVE)
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
