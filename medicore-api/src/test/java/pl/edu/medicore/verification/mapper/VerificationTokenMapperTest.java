package pl.edu.medicore.verification.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.edu.medicore.verification.dto.VerificationTokenCreateDto;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VerificationTokenMapperTest {
    private VerificationTokenMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(VerificationTokenMapper.class);
    }

    @Test
    void shouldMapToEntity_whenInputIsValid() {
        VerificationTokenCreateDto dto = new VerificationTokenCreateDto(TokenType.EMAIL_VERIFICATION,
                "abc", "test@gmail.com", Duration.ofMinutes(5));

        VerificationToken entity = mapper.toEntity(dto);
        assertEquals(TokenType.EMAIL_VERIFICATION, entity.getTokenType());
        assertEquals("test@gmail.com", entity.getEmail());
    }

    @Test
    void shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }
}
