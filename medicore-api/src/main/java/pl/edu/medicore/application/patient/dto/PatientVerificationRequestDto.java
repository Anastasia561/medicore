package pl.edu.medicore.application.patient.dto;

import jakarta.validation.constraints.NotBlank;

public record PatientVerificationRequestDto(
        @NotBlank(message = "Token is required")
        String token
) {
}
