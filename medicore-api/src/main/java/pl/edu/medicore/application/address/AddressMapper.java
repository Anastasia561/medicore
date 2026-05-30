package pl.edu.medicore.application.address;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.application.address.dto.PatientAddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(source = "city.name", target = "city")
    @Mapping(source = "city.country.name", target = "country")
    PatientAddressDto addressToPatientAddressDto(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "city", target = "city.name")
    @Mapping(source = "country", target = "city.country.name")
    Address toEntity(PatientAddressDto dto);
}
