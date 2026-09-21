package pl.edu.medicore.application.test.dto;

import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

public record TestResponseDto(
        HashId id,
        LocalDate date
) {
}
