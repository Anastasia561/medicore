package pl.edu.medicore.profile.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.service.PersonService;
import pl.edu.medicore.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;
import pl.edu.medicore.profile.mapper.ProfileMapper;

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
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void shouldReturnPatientProfile_whenRoleIsPatient() {
        Patient patient = new Patient();
        PatientProfileResponseDto dto = new PatientProfileResponseDto();

        when(personService.getRoleById(1L)).thenReturn(Role.PATIENT);
        when(patientService.getById(1L)).thenReturn(patient);
        when(profileMapper.toPatientDto(patient)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(1L);

        assertEquals(dto, result);

        verify(patientService).getById(1L);
        verify(profileMapper).toPatientDto(patient);
        verifyNoInteractions(doctorService);
    }

    @Test
    void shouldReturnDoctorProfile_whenRoleIsPatient() {
        Doctor doctor = new Doctor();
        DoctorProfileResponseDto dto = new DoctorProfileResponseDto();

        when(personService.getRoleById(1L)).thenReturn(Role.DOCTOR);
        when(doctorService.getById(1L)).thenReturn(doctor);
        when(profileMapper.toDoctorDto(doctor)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(1L);

        assertEquals(dto, result);

        verify(doctorService).getById(1L);
        verify(profileMapper).toDoctorDto(doctor);
        verifyNoInteractions(patientService);
    }

    @Test
    void shouldReturnPersonProfile_whenRoleIsAdmin() {
        Person person = new Person();
        ProfileResponseDto dto = new ProfileResponseDto();

        when(personService.getRoleById(1L)).thenReturn(Role.ADMIN);
        when(personService.getById(1L)).thenReturn(person);
        when(profileMapper.toDto(person)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(1L);

        assertEquals(dto, result);

        verify(personService).getById(1L);
        verify(profileMapper).toDto(person);
        verifyNoInteractions(doctorService, patientService);
    }

    @Test
    void shouldUpdateProfile_whenInputIsValid() {
        long id = 1L;
        ProfileUpdateDto dto = new ProfileUpdateDto("testF", "testL");
        Person person = new Person();

        when(personService.getById(id)).thenReturn(person);

        long result = profileService.updateProfile(dto, id);

        assertEquals(id, result);

        verify(personService).getById(id);
        verify(profileMapper).updatePersonFromDto(dto, person);
        verifyNoInteractions(doctorService, patientService);
    }

    @Test
    void shouldUpdatePatientProfile_whenInputIsValid() {
        long id = 1L;
        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("testF", "lestL",
                LocalDate.of(2026, 10, 2), "123",
                new PatientAddressDto("poland", "warsaw", "test street", 10));
        Patient patient = new Patient();

        when(patientService.getById(id)).thenReturn(patient);

        long result = profileService.updatePatientProfile(dto, id);

        assertEquals(id, result);

        verify(patientService).getById(id);
        verify(profileMapper).updatePatientFromDto(dto, patient);
        verifyNoInteractions(doctorService, personService);
    }
}
