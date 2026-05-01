package pl.edu.medicore.appointment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.patient.model.Patient;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(source = "patient.firstName", target = "firstName")
    @Mapping(source = "patient.lastName", target = "lastName")
    @Mapping(source = "patient.phoneNumber", target = "phoneNumber")
    AppointmentForDoctorDto toDoctorDto(Appointment appointment);

    @Mapping(source = "doctor.firstName", target = "firstName")
    @Mapping(source = "doctor.lastName", target = "lastName")
    @Mapping(source = "doctor.specialization", target = "specialization")
    AppointmentForPatientDto toPatientDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(source = "doctor", target = "doctor")
    @Mapping(source = "patient", target = "patient")
    Appointment toEntity(AppointmentCreateDto dto, Doctor doctor, Patient patient);

    @Mapping(source = "doctor.firstName", target = "doctorFirstName")
    @Mapping(source = "doctor.lastName", target = "doctorLastName")
    @Mapping(source = "patient.firstName", target = "patientFirstName")
    @Mapping(source = "patient.lastName", target = "patientLastName")
    @Mapping(source = "doctor.specialization", target = "specialization")
    @Mapping(target = "date", source = "date", dateFormat = "dd MMM yyyy")
    @Mapping(target = "time", source = "time", dateFormat = "HH:mm")
    AppointmentNotificationEmailDto toEmailDto(Appointment appointment);
}
