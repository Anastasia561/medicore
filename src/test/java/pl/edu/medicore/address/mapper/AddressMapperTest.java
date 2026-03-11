package pl.edu.medicore.address.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.address.model.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressMapperTest {
    private AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        addressMapper = Mappers.getMapper(AddressMapper.class);
    }

    @Test
    void shouldMapAddressDtoToEntity_whenInputIsValid() {
        PatientAddressDto dto = new PatientAddressDto("Poland", "Warsaw",
                "Test street", 10);
        Address result = addressMapper.toEntity(dto);
        assertEquals("Poland", result.getCity().getCountry().getName());
        assertEquals("Warsaw", result.getCity().getName());
        assertEquals("Test street", result.getStreet());
        assertEquals(10, result.getNumber());
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(addressMapper.toEntity(null));
    }
}
