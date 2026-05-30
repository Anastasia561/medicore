package pl.edu.medicore.application.record;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.appointment.Appointment;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.application.record.dto.RecordDto;
import pl.edu.medicore.application.record.dto.RecordForDoctorPreviewDto;
import pl.edu.medicore.application.record.dto.RecordForPatientPreviewDto;

@Mapper(componentModel = "spring")
public interface RecordMapper {

    @Mapping(source = "appointment.doctor", target = "doctor")
    @Mapping(source = "appointment.patient", target = "patient")
    @Mapping(source = "appointment.date", target = "date")
    @Mapping(source = "prescriptions", target = "prescriptions")
    RecordDto toDto(Record record);

    @Mapping(source = "appointment.doctor", target = "doctor")
    @Mapping(source = "appointment.date", target = "date")
    RecordForPatientPreviewDto toPatientPreviewDto(Record record);

    @Mapping(source = "appointment.patient", target = "patient")
    @Mapping(source = "appointment.date", target = "date")
    RecordForDoctorPreviewDto toDoctorPreviewDto(Record record);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "appointment", target = "appointment")
    Record toEntity(RecordCreateDto recordCreateDto, Appointment appointment);
}
