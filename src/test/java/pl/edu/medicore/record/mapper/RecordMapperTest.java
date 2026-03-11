package pl.edu.medicore.record.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.record.dto.RecordForPatientPreviewDto;
import pl.edu.medicore.record.model.Record;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RecordMapperTest {
    private RecordMapper recordMapper;

    @BeforeEach
    void setUp() {
        recordMapper = Mappers.getMapper(RecordMapper.class);
    }

    @Test
    void shouldMapToDto_whenInputIsValid() {
        Record record = new Record();
        record.setDiagnosis("Test diagnosis");
        record.setSummary("Test summary");
        Prescription prescription = new Prescription();
        prescription.setId(1L);
        prescription.setMedicine("Test medicine");
        record.setPrescriptions(List.of(prescription));

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.of(2026, 10, 10));
        Doctor doctor = new Doctor();
        doctor.setFirstName("Test doctor");
        Patient patient = new Patient();
        patient.setFirstName("Test patient");
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        record.setAppointment(appointment);

        RecordDto dto = recordMapper.toDto(record);
        assertEquals("Test diagnosis", dto.diagnosis());
        assertEquals("Test summary", dto.summary());
        assertEquals(LocalDate.of(2026, 10, 10), dto.date());
        assertEquals(doctor.getFirstName(), dto.doctor().firstName());
        assertEquals(patient.getFirstName(), dto.patient().firstName());
        assertEquals(prescription.getMedicine(), dto.prescriptions().getFirst().medicine());
    }

    @Test
    void shouldMapToPatientPreviewDto_whenInputIsValid() {
        Record record = new Record();

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.of(2026, 10, 10));
        Doctor doctor = new Doctor();
        doctor.setFirstName("Test doctor");
        appointment.setDoctor(doctor);
        record.setAppointment(appointment);

        RecordForPatientPreviewDto dto = recordMapper.toPatientPreviewDto(record);
        assertEquals(LocalDate.of(2026, 10, 10), dto.getDate());
        assertEquals(doctor.getFirstName(), dto.getDoctor().firstName());
    }

    @Test
    void shouldMapToDoctorPreviewDto_whenInputIsValid() {
        Record record = new Record();

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.of(2026, 10, 10));
        Patient patient = new Patient();
        patient.setFirstName("Test patient");
        appointment.setPatient(patient);
        record.setAppointment(appointment);

        RecordForDoctorPreviewDto dto = recordMapper.toDoctorPreviewDto(record);
        assertEquals(LocalDate.of(2026, 10, 10), dto.getDate());
        assertEquals(patient.getFirstName(), dto.getPatient().firstName());
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        RecordCreateDto dto = new RecordCreateDto(1L, "Test diagnosis",
                "Test summary");
        Appointment appointment = new Appointment();

        Record entity = recordMapper.toEntity(dto, appointment);
        assertEquals("Test diagnosis", entity.getDiagnosis());
        assertEquals("Test summary", entity.getSummary());
        assertEquals(appointment, entity.getAppointment());
    }

    @Test
    void shouldReturnNull_whenRecordToDtoIsNull() {
        assertNull(recordMapper.toDto(null));
    }

    @Test
    void shouldReturnNull_whenRecordToPatientDtoIsNull() {
        assertNull(recordMapper.toPatientPreviewDto(null));
    }

    @Test
    void shouldReturnNull_whenRecordToDoctorDtoIsNull() {
        assertNull(recordMapper.toDoctorPreviewDto(null));
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(recordMapper.toEntity(null, null));
    }
}
