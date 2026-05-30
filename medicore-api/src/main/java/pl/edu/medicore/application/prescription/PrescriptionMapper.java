package pl.edu.medicore.application.prescription;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.application.record.Record;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "record", target = "record")
    Prescription toEntity(PrescriptionCreateDto dto, Record record);
}
