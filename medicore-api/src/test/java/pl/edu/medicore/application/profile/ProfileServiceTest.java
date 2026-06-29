package pl.edu.medicore.application.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.doctor.DoctorService;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.Person;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.PersonService;
import pl.edu.medicore.application.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileResponseDto;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.infrastructure.messaging.event.PatientUpdateEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private PersonService personService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void shouldReturnPersonProfile_whenRoleIsPatient() {
        long id = 1L;
        HashId hashId = new HashId(id);

        Patient patient = new Patient();
        ProfileResponseDto dto = new ProfileResponseDto();

        when(personService.getById(hashId)).thenReturn(patient);
        when(profileMapper.toDto(patient)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(hashId, Role.PATIENT);

        assertEquals(dto, result);

        verify(personService).getById(hashId);
        verify(profileMapper).toDto(patient);
        verifyNoInteractions(doctorService);
    }

    @Test
    void shouldReturnDoctorProfile_whenRoleIsDoctor() {
        long doctorId = 1L;
        HashId hashId = new HashId(doctorId);

        Doctor doctor = new Doctor();
        DoctorProfileResponseDto dto = new DoctorProfileResponseDto();

        when(doctorService.getById(hashId)).thenReturn(doctor);
        when(profileMapper.toDoctorDto(doctor)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(hashId, Role.DOCTOR);

        assertEquals(dto, result);

        verify(doctorService).getById(hashId);
        verify(profileMapper).toDoctorDto(doctor);
        verifyNoInteractions(personService);
    }

    @Test
    void shouldReturnPersonProfile_whenRoleIsAdmin() {
        HashId hashId = new HashId(1L);

        Person person = new Person();
        ProfileResponseDto dto = new ProfileResponseDto();

        when(personService.getById(hashId)).thenReturn(person);
        when(profileMapper.toDto(person)).thenReturn(dto);

        ProfileResponseDto result = profileService.getProfileById(hashId, Role.ADMIN);

        assertEquals(dto, result);

        verify(personService).getById(hashId);
        verify(profileMapper).toDto(person);
        verifyNoInteractions(doctorService);
    }

    @Test
    void shouldUpdatePatientProfile_whenInputIsValid() {
        long id = 1L;
        HashId hashId = new HashId(id);

        ProfileUpdateDto dto = new ProfileUpdateDto("test", "testL",
                Gender.MALE, "1234",
                new AddressDto("test country", "test city", "test street", "10"));

        Patient patient = new Patient();

        when(personService.getById(hashId)).thenReturn(patient);

        profileService.updateProfile(dto, hashId, Role.PATIENT);

        verify(personService).getById(hashId);
        verify(profileMapper).updatePersonFromDto(dto, patient);
        verify(publisher).publishEvent(any(PatientUpdateEvent.class));

        verifyNoMoreInteractions(personService);
        verifyNoInteractions(doctorService);
    }

    @Test
    void shouldUpdateDoctorProfile_whenInputIsValid() {
        long id = 1L;
        HashId hashId = new HashId(id);

        ProfileUpdateDto dto = new ProfileUpdateDto("test", "testL",
                Gender.MALE, "1234",
                new AddressDto("test country", "test city", "test street", "10"));

        Patient patient = new Patient();

        when(personService.getById(hashId)).thenReturn(patient);

        profileService.updateProfile(dto, hashId, Role.DOCTOR);

        verify(personService).getById(hashId);
        verify(profileMapper).updatePersonFromDto(dto, patient);
        verifyNoInteractions(doctorService, publisher);
    }
}
