package pl.edu.medicore.application.person;

import org.mapstruct.Mapper;
import pl.edu.medicore.application.email.dto.ConfirmationEmailDto;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    ConfirmationEmailDto toEmailDto(Person person);
}
