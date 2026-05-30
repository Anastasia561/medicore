package pl.edu.medicore.application.doctor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.application.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.common.encryption.HashIdMapper;

@Mapper(componentModel = "spring", uses = HashIdMapper.class)
public interface DoctorMapper {

    DoctorResponseDto toDoctorResponseDto(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "DOCTOR")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "employmentDate", expression = "java(java.time.LocalDate.now())")
    Doctor toEntity(DoctorRegistrationDto dto);

    ConfirmationEmailDto toEmailDto(Doctor doctor);
}
