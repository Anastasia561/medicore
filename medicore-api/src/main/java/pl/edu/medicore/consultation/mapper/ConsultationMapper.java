package pl.edu.medicore.consultation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.email.dto.ScheduleEmailDto;

@Mapper(componentModel = "spring")
public interface ConsultationMapper {

    @Mapping(source = "workday", target = "day")
    ConsultationDto toDto(Consultation consultation);

    @Mapping(source = "dto.day", target = "workday")
    @Mapping(source = "doctor", target = "doctor")
    Consultation toEntity(ConsultationCreateDto dto, Doctor doctor);

    void updateConsultationFromDto(ConsultationUpdateDto dto, @MappingTarget Consultation consultation);

    @Mapping(source = "doctor.firstName", target = "firstName")
    @Mapping(source = "doctor.lastName", target = "lastName")
    @Mapping(source = "workday", target = "day")
    ScheduleEmailDto toEmailDto(Consultation consultation);
}
