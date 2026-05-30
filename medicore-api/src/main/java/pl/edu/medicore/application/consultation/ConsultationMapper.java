package pl.edu.medicore.application.consultation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.email.dto.ScheduleEmailDto;
import pl.edu.medicore.common.encryption.HashIdMapper;

@Mapper(componentModel = "spring", uses = HashIdMapper.class)
public interface ConsultationMapper {

    @Mapping(source = "workday", target = "day")
    ConsultationDto toDto(Consultation consultation);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.day", target = "workday")
    @Mapping(source = "doctor", target = "doctor")
    Consultation toEntity(ConsultationCreateDto dto, Doctor doctor);

    void updateConsultationFromDto(ConsultationUpdateDto dto, @MappingTarget Consultation consultation);

    @Mapping(source = "doctor.firstName", target = "firstName")
    @Mapping(source = "doctor.lastName", target = "lastName")
    @Mapping(source = "workday", target = "day")
    ScheduleEmailDto toEmailDto(Consultation consultation);
}
