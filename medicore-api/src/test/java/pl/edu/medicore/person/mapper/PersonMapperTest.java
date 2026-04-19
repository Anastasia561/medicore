package pl.edu.medicore.person.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.email.dto.ConfirmationEmailDto;
import pl.edu.medicore.person.model.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PersonMapperTest {
    private PersonMapper personMapper;

    @BeforeEach
    public void setUp() {
        personMapper = Mappers.getMapper(PersonMapper.class);
    }

    @Test
    void shouldMapToEmailDto_whenInputIsValid() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        ConfirmationEmailDto emailDto = personMapper.toEmailDto(person);

        assertEquals("John", emailDto.firstName());
        assertEquals("Doe", emailDto.lastName());
    }

    @Test
    void shouldReturnNull_whenPersonIsNull() {
        assertNull(personMapper.toEmailDto(null));
    }
}
