package pl.edu.medicore.application.patient;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.AddressMapper;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface PatientMapper {
    PatientResponseDto toPatientResponseDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "PATIENT")
    @Mapping(target = "status", constant = "UNVERIFIED")
    @Mapping(target = "address", source = "address")
    Patient toEntity(PatientRegisterDto dto);

    ConfirmationEmailDto toEmailDto(Patient patient);
}
