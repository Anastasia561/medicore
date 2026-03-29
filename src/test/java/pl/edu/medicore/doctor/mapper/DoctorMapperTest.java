package pl.edu.medicore.doctor.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DoctorMapperTest {
    private DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() {
        doctorMapper = Mappers.getMapper(DoctorMapper.class);
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
        assertEquals(Status.ACTIVE, entity.getStatus());
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
