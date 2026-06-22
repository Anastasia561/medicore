package pl.edu.medicore.application.patient;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.common.encryption.HashIdMapper;

@Mapper(componentModel = "spring", uses = {HashIdMapper.class, AddressMapper.class})
public interface PatientMapper {
    PatientResponseDto toPatientResponseDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "PATIENT")
    @Mapping(target = "status", constant = "UNVERIFIED")
    Patient toEntity(PatientRegisterDto dto);

    ConfirmationEmailDto toEmailDto(Patient patient);
}
