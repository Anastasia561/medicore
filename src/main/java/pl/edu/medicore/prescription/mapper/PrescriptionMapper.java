package pl.edu.medicore.prescription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.model.Prescription;
import pl.edu.medicore.record.model.Record;

@Mapper(componentModel = "spring")
public interface PrescriptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "record", target = "record")
    Prescription toEntity(PrescriptionCreateDto dto, Record record);
}
