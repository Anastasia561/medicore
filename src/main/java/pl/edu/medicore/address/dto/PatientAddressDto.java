package pl.edu.medicore.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PatientAddressDto(
        @NotBlank(message = "Country name is required")
        @Size(max = 30, message = "Country name must be at most 30 characters")
        String country,
        @NotBlank(message = "City name is required")
        @Size(max = 30, message = "City name must be at most 30 characters")
        String city,
        @NotBlank(message = "City name is required")
        @Size(max = 40, message = "City name must be at most 30 characters")
        String street,
        @NotNull(message = "House number is required")
        @Positive(message = "House number must be greater than 0")
        Integer number
) {
}
