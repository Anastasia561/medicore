package pl.edu.medicore.patient.dto;

import pl.edu.medicore.address.dto.PatientAddressDto;

import java.time.LocalDate;

public record PatientResponseDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        PatientAddressDto address
) {
}
