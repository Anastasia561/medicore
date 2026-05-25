package pl.edu.medicore.application.verification;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface VerificationTokenMapper {

    @Mapping(target = "expiresAt", expression = "java(Instant.now().plus(dto.validDuration()))")
    VerificationToken toEntity(VerificationTokenCreateDto dto);
}