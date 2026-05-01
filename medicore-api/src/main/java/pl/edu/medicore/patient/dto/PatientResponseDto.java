package pl.edu.medicore.patient.dto;

import pl.edu.medicore.address.dto.PatientAddressDto;

import java.time.LocalDate;
import java.util.UUID;

public record PatientResponseDto(
        UUID publicId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        PatientAddressDto address
) {
}
