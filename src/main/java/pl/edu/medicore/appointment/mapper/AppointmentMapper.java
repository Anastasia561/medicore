package pl.edu.medicore.appointment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.appointment.dto.AppointmentForDoctorDto;
import pl.edu.medicore.appointment.dto.AppointmentForPatientDto;
import pl.edu.medicore.appointment.model.Appointment;

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
}
