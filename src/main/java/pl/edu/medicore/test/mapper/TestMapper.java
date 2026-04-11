package pl.edu.medicore.test.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.model.Test;

@Mapper(componentModel = "spring")
public interface TestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patient", target = "patient")
    Test toEntity(TestUploadRequestDto dto, Patient patient);
}
