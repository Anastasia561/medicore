package pl.edu.medicore.doctor.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;

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
    void shouldReturnNull_whenDoctorIsNull() {
        assertNull(doctorMapper.toDoctorResponseDto(null));
    }
}
