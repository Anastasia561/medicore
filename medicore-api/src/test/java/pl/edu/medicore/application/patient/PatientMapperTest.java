package pl.edu.medicore.application.patient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.address.Address;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.UserStatus;
import pl.edu.medicore.common.encryption.HashIdMapper;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PatientMapperTest {
    private PatientMapper patientMapper;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        patientMapper = Mappers.getMapper(PatientMapper.class);
        AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);
        HashIdMapper hashIdMapper = new HashIdMapper();

        Field field = patientMapper.getClass().getDeclaredField("addressMapper");
        field.setAccessible(true);
        field.set(patientMapper, addressMapper);

        Field hashIdMapperField = patientMapper.getClass().getDeclaredField("hashIdMapper");
        hashIdMapperField.setAccessible(true);
        hashIdMapperField.set(patientMapper, hashIdMapper);
    }

    @Test
    void shouldMapToPatientResponseDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("test@gmail.com");
        patient.setPhoneNumber("1234567890");
        patient.setBirthDate(LocalDate.of(1990, 10, 10));

        Address address = new Address();
        address.setStreet("Test street");
        patient.setAddress(address);

        PatientResponseDto result = patientMapper.toPatientResponseDto(patient);

        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("test@gmail.com", result.email());
        assertEquals("1234567890", result.phoneNumber());
        assertEquals(LocalDate.of(1990, 10, 10), result.birthDate());
        assertEquals("Test street", result.address().street());
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        AddressDto address = new AddressDto("Poland", "Warsaw",
                "Test street", "10");
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "123", "123", Gender.MALE, 56.8, 167.9,
                PregnancyStatus.NOT_APPLICABLE, LocalDate.of(1990, 10, 10),
                "123456", address);

        Patient entity = patientMapper.toEntity(dto);
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals(UserStatus.UNVERIFIED, entity.getStatus());
        assertEquals(Role.PATIENT, entity.getRole());
        assertEquals("Poland", entity.getAddress().getCity().getCountry().getName());
    }

    @Test
    void shouldMapToEmailDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");

        ConfirmationEmailDto dto = patientMapper.toEmailDto(patient);

        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
    }

    @Test
    void shouldReturnNull_whenPatientIsNullForEmailDto() {
        assertNull(patientMapper.toEmailDto(null));
    }

    @Test
    void shouldReturnNull_whenPatientIsNull() {
        assertNull(patientMapper.toPatientResponseDto(null));
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(patientMapper.toEntity(null));
    }
}
