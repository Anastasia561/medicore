package pl.edu.medicore.patient.mapper;

import org.mapstruct.Mapper;
import pl.edu.medicore.address.mapper.AddressMapper;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.model.Patient;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface PatientMapper {
    PatientResponseDto patientToPatientResponseDto(Patient patient);
}
