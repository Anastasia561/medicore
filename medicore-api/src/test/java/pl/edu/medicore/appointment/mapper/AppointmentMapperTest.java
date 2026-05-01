package pl.edu.medicore.appointment.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.patient.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AppointmentMapperTest {
    private AppointmentMapper appointmentMapper;

    @BeforeEach
    public void setup() {
        appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
    }

    @Test
    void shouldMapToDoctorAppointmentDto_whenInputIsValid() {
        Appointment appointment = new Appointment();
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setPhoneNumber("1234567890");

        appointment.setDate(LocalDate.of(2026, 10, 12));
        appointment.setTime(LocalTime.of(20, 10));
        appointment.setStatus(Status.COMPLETED);
        appointment.setPatient(patient);

        AppointmentForDoctorDto result = appointmentMapper.toDoctorDto(appointment);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("1234567890", result.getPhoneNumber());
        assertEquals(LocalDate.of(2026, 10, 12), result.getDate());
        assertEquals(LocalTime.of(20, 10), result.getTime());
        assertEquals(Status.COMPLETED, result.getStatus());
    }

    @Test
    void shouldMapToPatientAppointmentDto_whenInputIsValid() {
        Appointment appointment = new Appointment();
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialization(Specialization.DERMATOLOGIST);

        appointment.setDate(LocalDate.of(2026, 10, 12));
        appointment.setTime(LocalTime.of(20, 10));
        appointment.setStatus(Status.COMPLETED);
        appointment.setDoctor(doctor);

        AppointmentForPatientDto result = appointmentMapper.toPatientDto(appointment);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Status.COMPLETED, result.getStatus());
        assertEquals(LocalDate.of(2026, 10, 12), result.getDate());
        assertEquals(LocalTime.of(20, 10), result.getTime());
        assertEquals(Specialization.DERMATOLOGIST, result.getSpecialization());
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        AppointmentCreateDto dto = new AppointmentCreateDto(UUID.randomUUID(),
                LocalDate.of(2026, 10, 12), LocalTime.of(20, 10));

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Patient patient = new Patient();
        patient.setId(2L);

        Appointment result = appointmentMapper.toEntity(dto, doctor, patient);
        assertEquals(1L, result.getDoctor().getId());
        assertEquals(2L, result.getPatient().getId());
        assertEquals(Status.SCHEDULED, result.getStatus());
        assertEquals(LocalDate.of(2026, 10, 12), result.getDate());
        assertEquals(LocalTime.of(20, 10), result.getTime());
    }

    @Test
    void shouldMapToAppointmentNotificationDto_whenInputIsValid() {
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");

        Doctor doctor = new Doctor();
        doctor.setFirstName("Kevin");
        doctor.setLastName("Lee");
        doctor.setSpecialization(Specialization.DERMATOLOGIST);

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.of(2026, 10, 12));
        appointment.setTime(LocalTime.of(20, 10));
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

        AppointmentNotificationEmailDto dto = appointmentMapper.toEmailDto(appointment);
        assertEquals("John", dto.patientFirstName());
        assertEquals("Doe", dto.patientLastName());
        assertEquals("Kevin", dto.doctorFirstName());
        assertEquals("Lee", dto.doctorLastName());
        assertEquals(LocalDate.of(2026, 10, 12).format(dateFormatter), dto.date());
        assertEquals(LocalTime.of(20, 10).format(timeFormatter), dto.time());
    }

    @Test
    void shouldReturnNull_whenAppointmentForNotificationEmailDtoIsNull() {
        assertNull(appointmentMapper.toEmailDto(null));
    }

    @Test
    void shouldReturnNull_whenAppointmentForDoctorIsNull() {
        assertNull(appointmentMapper.toDoctorDto(null));
    }

    @Test
    void shouldReturnNull_whenAppointmentForPatientIsNull() {
        assertNull(appointmentMapper.toPatientDto(null));
    }

    @Test
    void shouldReturnNull_whenInputsForEntityNull() {
        assertNull(appointmentMapper.toEntity(null, null, null));
    }
}
