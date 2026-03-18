package pl.edu.medicore.consultation.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.model.Workday;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.email.dto.ScheduleEmailDto;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConsultationMapperTest {
    private ConsultationMapper consultationMapper;

    @BeforeEach
    void setUp() {
        consultationMapper = Mappers.getMapper(ConsultationMapper.class);
    }

    @Test
    void shouldMapToDto_whenInputIsValid() {
        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(8, 30));
        consultation.setEndTime(LocalTime.of(17, 30));
        consultation.setWorkday(Workday.FRIDAY);

        ConsultationDto result = consultationMapper.toDto(consultation);
        assertEquals(LocalTime.of(8, 30), result.startTime());
        assertEquals(LocalTime.of(17, 30), result.endTime());
        assertEquals(Workday.FRIDAY, result.day());
    }

    @Test
    void shouldMapToEmailDto_whenInputIsValid() {
        Consultation consultation = new Consultation();
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        consultation.setWorkday(Workday.FRIDAY);
        consultation.setDoctor(doctor);

        ScheduleEmailDto result = consultationMapper.toEmailDto(consultation);
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals(Workday.FRIDAY, result.day());
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        ConsultationCreateDto dto = new ConsultationCreateDto(1L, Workday.FRIDAY,
                LocalTime.of(8, 30), LocalTime.of(17, 30));

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Consultation entity = consultationMapper.toEntity(dto, doctor);
        assertEquals(LocalTime.of(8, 30), entity.getStartTime());
        assertEquals(LocalTime.of(17, 30), entity.getEndTime());
        assertEquals(Workday.FRIDAY, entity.getWorkday());
        assertEquals(doctor, entity.getDoctor());
    }

    @Test
    void shouldUpdateConsultation_whenInputIsValid() {
        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(8, 30),
                LocalTime.of(17, 30));

        Consultation consultation = new Consultation();
        consultation.setStartTime(LocalTime.of(7, 30));
        consultation.setEndTime(LocalTime.of(10, 30));

        consultationMapper.updateConsultationFromDto(dto, consultation);
        assertEquals(LocalTime.of(8, 30), consultation.getStartTime());
        assertEquals(LocalTime.of(17, 30), consultation.getEndTime());
    }

    @Test
    void shouldReturnNull_whenConsultationIsNullForDto() {
        assertNull(consultationMapper.toDto(null));
    }

    @Test
    void shouldReturnNull_whenConsultationIsNullForEmailDto() {
        assertNull(consultationMapper.toEmailDto(null));
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(consultationMapper.toEntity(null, null));
    }
}
