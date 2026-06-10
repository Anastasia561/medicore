package pl.edu.medicore.application.appointment;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.application.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.email.dto.AppointmentNotificationEmailDto;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.common.config.properties.SchedulingProperties;
import pl.edu.medicore.common.encryption.HashIdMapper;

@Mapper(componentModel = "spring", uses = HashIdMapper.class)
public abstract class AppointmentMapper {
    @Autowired
    protected SchedulingProperties schedulingProperties;

    @Mapping(source = "patient.firstName", target = "firstName")
    @Mapping(source = "patient.lastName", target = "lastName")
    @Mapping(source = "patient.phoneNumber", target = "phoneNumber")
    public abstract AppointmentForDoctorDto toDoctorDto(Appointment appointment);

    @Mapping(source = "doctor.firstName", target = "firstName")
    @Mapping(source = "doctor.lastName", target = "lastName")
    @Mapping(source = "doctor.specialization", target = "specialization")
    public abstract AppointmentForPatientDto toPatientDto(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(source = "doctor", target = "doctor")
    @Mapping(source = "patient", target = "patient")
    public abstract Appointment toEntity(AppointmentCreateDto dto, Doctor doctor, Patient patient);

    @Mapping(source = "doctor.firstName", target = "doctorFirstName")
    @Mapping(source = "doctor.lastName", target = "doctorLastName")
    @Mapping(source = "patient.firstName", target = "patientFirstName")
    @Mapping(source = "patient.lastName", target = "patientLastName")
    @Mapping(source = "doctor.specialization", target = "specialization")
    @Mapping(target = "date", source = "date", dateFormat = "dd MMM yyyy")
    @Mapping(target = "time", source = "startTime", dateFormat = "HH:mm")
    public abstract AppointmentNotificationEmailDto toEmailDto(Appointment appointment);

    @AfterMapping
    protected void calculateEndTime(AppointmentCreateDto dto, @MappingTarget Appointment appointment) {
        if (dto.startTime() != null) {
            appointment.setEndTime(dto.startTime().plusMinutes(schedulingProperties.getSlotDurationMinutes()));
        }
    }
}