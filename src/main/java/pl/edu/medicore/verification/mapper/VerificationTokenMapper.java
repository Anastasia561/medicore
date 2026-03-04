package pl.edu.medicore.verification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.medicore.verification.dto.VerificationTokenCreateDto;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface VerificationTokenMapper {

    @Mapping(target = "expiresAt", expression = "java(Instant.now().plus(dto.validDuration()))")
    VerificationToken toEntity(VerificationTokenCreateDto dto);
}