package pl.edu.medicore.application.person;

import org.mapstruct.Mapper;
import pl.edu.medicore.application.address.AddressMapper;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface PersonMapper {

    ConfirmationEmailDto toEmailDto(Person person);
}
