package pl.edu.medicore.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.model.Patient;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface PatientMapper {
    PatientResponseDto toPatientResponseDto(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "PATIENT")
    @Mapping(target = "status", constant = "UNVERIFIED")
    @Mapping(target = "address", source = "address")
    Patient toEntity(PatientRegisterDto dto);
}
