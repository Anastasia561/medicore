package pl.edu.medicore.application.test;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;

@Mapper(componentModel = "spring")
public interface TestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patient", target = "patient")
    Test toEntity(TestUploadRequestDto dto, Patient patient);
}
