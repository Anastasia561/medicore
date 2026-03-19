package pl.edu.medicore.profile.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.address.model.Address;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.profile.dto.DoctorProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileResponseDto;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

class ProfileMapperTest {
    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        profileMapper = Mappers.getMapper(ProfileMapper.class);
        AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

        Field field = profileMapper.getClass().getDeclaredField("addressMapper");
        field.setAccessible(true);
        field.set(profileMapper, addressMapper);
    }

    @Test
    void shouldMapToDto_whenInputIsValid() {
        Person person = new Person();
        person.setFirstName("Kevin");
        person.setLastName("Lee");
        person.setEmail("test@gmail.com");

        ProfileResponseDto dto = profileMapper.toDto(person);

        assertEquals("Kevin", dto.getFirstName());
        assertEquals("Lee", dto.getLastName());
        assertEquals("test@gmail.com", dto.getEmail());
    }

    @Test
    void shouldMapToDoctorProfileDto_whenInputIsValid() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Kevin");
        doctor.setLastName("Lee");
        doctor.setEmail("test@gmail.com");
        doctor.setEmploymentDate(LocalDate.of(1990, 10, 2));
        doctor.setExperience(10);
        doctor.setSpecialization(Specialization.DERMATOLOGIST);

        DoctorProfileResponseDto dto = profileMapper.toDoctorDto(doctor);

        assertEquals("Kevin", dto.getFirstName());
        assertEquals("Lee", dto.getLastName());
        assertEquals("test@gmail.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 10, 2), dto.getEmploymentDate());
        assertEquals(10, dto.getExperience());
        assertEquals(Specialization.DERMATOLOGIST, dto.getSpecialization());
    }

    @Test
    void shouldMapToPatientProfileDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setFirstName("Kevin");
        patient.setLastName("Lee");
        patient.setEmail("test@gmail.com");
        patient.setBirthDate(LocalDate.of(1990, 10, 2));
        patient.setPhoneNumber("123");
        Address address = new Address();
        address.setStreet("test");
        address.setNumber(10);
        patient.setAddress(address);

        PatientProfileResponseDto dto = profileMapper.toPatientDto(patient);

        assertEquals("Kevin", dto.getFirstName());
        assertEquals("Lee", dto.getLastName());
        assertEquals("test@gmail.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 10, 2), dto.getBirthDate());
        assertEquals("test", dto.getAddress().street());
        assertEquals("123", dto.getPhoneNumber());
    }

    @Test
    void shouldUpdatePerson_whenInputIsValid() {
        Person person = new Person();
        person.setFirstName("Kevin");
        person.setLastName("Lee");

        ProfileUpdateDto dto = new ProfileUpdateDto("test", "testL");
        profileMapper.updatePersonFromDto(dto, person);

        assertEquals("test", person.getFirstName());
        assertEquals("testL", person.getLastName());
    }

    @Test
    void shouldUpdatePatientFromDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setFirstName("Kevin");
        patient.setLastName("Lee");
        patient.setBirthDate(LocalDate.of(1990, 10, 2));
        patient.setPhoneNumber("123");
        Address address = new Address();
        address.setStreet("test");
        address.setNumber(10);
        patient.setAddress(address);

        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("test", "testL",
                LocalDate.of(1999, 10, 2), "1234",
                new PatientAddressDto("test country", "test city", "test street", 10));
        profileMapper.updatePatientFromDto(dto, patient);

        assertEquals("test", patient.getFirstName());
        assertEquals("testL", patient.getLastName());
        assertEquals(LocalDate.of(1999, 10, 2), patient.getBirthDate());
        assertEquals("test street", patient.getAddress().getStreet());
        assertEquals("1234", patient.getPhoneNumber());
    }

    @Test
    void shouldReturnNull_whenPersonIsNullForDto() {
        assertNull(profileMapper.toDto(null));
    }

    @Test
    void shouldReturnNull_whenDoctorIsNull() {
        assertNull(profileMapper.toDoctorDto(null));
    }

    @Test
    void shouldReturnNull_whenPatientIsNull() {
        assertNull(profileMapper.toPatientDto(null));
    }
}
