package pl.edu.medicore.doctor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorResponseDto toDoctorResponseDto(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "DOCTOR")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "employmentDate", expression = "java(java.time.LocalDate.now())")
    Doctor toEntity(DoctorRegistrationDto dto);
}
