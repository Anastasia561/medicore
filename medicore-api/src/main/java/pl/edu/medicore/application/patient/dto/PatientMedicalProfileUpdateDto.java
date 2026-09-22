package pl.edu.medicore.application.patient.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import pl.edu.medicore.application.patient.PregnancyStatus;

public record PatientMedicalProfileUpdateDto(
        @DecimalMin(value = "1.0", message = "Weight must be greater than 0")
        @DecimalMax(value = "500.0", message = "Weight must be less than 500")
        Double weight,

        @DecimalMin(value = "30.0", message = "Height must be greater than 30 cm")
        @DecimalMax(value = "300.0", message = "Height must be less than 300 cm")
        Double height,

        @NotNull(message = "Pregnancy status is required")
        PregnancyStatus pregnancyStatus
) {
}
