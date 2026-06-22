package pl.edu.medicore.application.patient.dto;

import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;

public record PatientResponseDto(
        HashId id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        AddressDto address
) {
}
