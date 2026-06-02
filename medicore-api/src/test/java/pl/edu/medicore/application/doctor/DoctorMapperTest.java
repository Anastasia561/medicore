package pl.edu.medicore.application.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.application.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.application.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.person.UserStatus;
import pl.edu.medicore.common.encryption.HashIdMapper;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DoctorMapperTest {
    private DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        doctorMapper = Mappers.getMapper(DoctorMapper.class);
        HashIdMapper hashIdMapper = new HashIdMapper();

        Field hashIdMapperField = doctorMapper.getClass().getDeclaredField("hashIdMapper");
        hashIdMapperField.setAccessible(true);
        hashIdMapperField.set(doctorMapper, hashIdMapper);
    }

    @Test
    void shouldMapToDoctorResponseDto_whenInputIsValid() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setEmail("test@gmail.com");
        doctor.setSpecialization(Specialization.DERMATOLOGIST);
        doctor.setExperience(10);
        doctor.setEmploymentDate(LocalDate.of(2023, 1, 1));

        DoctorResponseDto result = doctorMapper.toDoctorResponseDto(doctor);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("test@gmail.com", result.email());
        assertEquals(Specialization.DERMATOLOGIST, result.specialization());
        assertEquals(10, result.experience());
        assertEquals(LocalDate.of(2023, 1, 1), result.employmentDate());
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token", "test@gmail.com",
                "John", "Doe", "pass", "pass", Gender.MALE, 10,
                Specialization.DERMATOLOGIST);

        Doctor entity = doctorMapper.toEntity(dto);
        assertEquals("John", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals(UserStatus.ACTIVE, entity.getStatus());
        assertEquals(Role.DOCTOR, entity.getRole());
        assertEquals(Specialization.DERMATOLOGIST, entity.getSpecialization());
    }

    @Test
    void shouldMapToEmailDto_whenInputIsValid() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        ConfirmationEmailDto dto = doctorMapper.toEmailDto(doctor);

        assertEquals("John", dto.firstName());
        assertEquals("Doe", dto.lastName());
    }

    @Test
    void shouldReturnNull_whenPatientIsNullForEmailDto() {
        assertNull(doctorMapper.toEmailDto(null));
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(doctorMapper.toEntity(null));
    }

    @Test
    void shouldReturnNull_whenDoctorIsNull() {
        assertNull(doctorMapper.toDoctorResponseDto(null));
    }
}
