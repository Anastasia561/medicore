package pl.edu.medicore.application.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.application.dto.PatientAddressDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.patient.PatientService;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.application.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.application.profile.dto.PatientProfileResponseDto;
import pl.edu.medicore.application.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private PersonService personService;
    @Mock
    private PatientService patientService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void shouldReturnPatientProfile_whenRoleIsPatient() {
        long id = 1L;

        Patient patient = new Patient();
        PatientProfileResponseDto dto = new PatientProfileResponseDto();

        when(patientService.getById(id)).thenReturn(patient);
        when(profileMapper.toPatientDto(patient)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(id, Role.PATIENT);

        assertEquals(dto, result);

        verify(patientService).getById(id);
        verify(profileMapper).toPatientDto(patient);
        verifyNoInteractions(doctorService);
    }

    @Test
    void shouldReturnDoctorProfile_whenRoleIsDoctor() {
        long doctorId = 1L;
        Doctor doctor = new Doctor();
        DoctorProfileResponseDto dto = new DoctorProfileResponseDto();

        when(doctorService.getById(doctorId)).thenReturn(doctor);
        when(profileMapper.toDoctorDto(doctor)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(1L, Role.DOCTOR);

        assertEquals(dto, result);

        verify(doctorService).getById(doctorId);
        verify(profileMapper).toDoctorDto(doctor);
        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturnPersonProfile_whenRoleIsAdmin() {
        Person person = new Person();
        ProfileResponseDto dto = new ProfileResponseDto();

        when(personService.getById(1L)).thenReturn(person);
        when(profileMapper.toDto(person)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(1L, Role.ADMIN);

        assertEquals(dto, result);

        verify(personService).getById(1L);
        verify(profileMapper).toDto(person);
        verifyNoInteractions(doctorService, patientService);
    }

    @Test
    void shouldUpdateProfile_whenInputIsValid() {
        long patientId = 1L;
        ProfileUpdateDto dto = new ProfileUpdateDto("testF", "testL");
        Person person = new Person();

        when(personService.getById(patientId)).thenReturn(person);

        profileService.updateProfile(dto, patientId);

        verify(personService).getById(patientId);
        verify(profileMapper).updatePersonFromDto(dto, person);
        verifyNoInteractions(doctorService, patientService, publisher);
    }

    @Test
    void shouldUpdatePatientProfile_whenInputIsValid() {
        long id = 1L;
        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("test", "testL",
                Gender.MALE, 50.7, 100.7, false,
                LocalDate.of(1999, 10, 2), "1234",
                new PatientAddressDto("test country", "test city", "test street", 10));

        Patient patient = new Patient();

        when(patientService.getById(id)).thenReturn(patient);

        profileService.updatePatientProfile(dto, id);

        verify(patientService).getById(id);
        verify(profileMapper).updatePatientFromDto(dto, patient);
        verifyNoInteractions(doctorService, personService);
    }
}
