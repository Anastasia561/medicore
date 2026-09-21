package pl.edu.medicore.application.test;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.patient.Patient;
import pl.edu.medicore.application.test.dto.TestResponseDto;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashIdMapper;

@Mapper(componentModel = "spring", uses = HashIdMapper.class)
public interface TestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "patient", target = "patient")
    @Mapping(target = "storageKey", expression = "java(java.util.UUID.randomUUID())")
    Test toEntity(TestUploadRequestDto dto, Patient patient);

    TestResponseDto toDto(Test test);
}
