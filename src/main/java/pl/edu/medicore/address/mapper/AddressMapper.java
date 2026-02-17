package pl.edu.medicore.address.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.address.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(source = "city.name", target = "city")
    @Mapping(source = "city.country.name", target = "country")
    PatientAddressDto addressToPatientAddressDto(Address address);
}
