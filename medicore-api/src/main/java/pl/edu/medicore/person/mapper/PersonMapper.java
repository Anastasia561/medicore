package pl.edu.medicore.person.mapper;

import org.mapstruct.Mapper;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.person.model.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    ConfirmationEmailDto toEmailDto(Person person);
}
