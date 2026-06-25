package pl.edu.medicore.application.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message = "Country name is required")
        @Size(min = 3, max = 30, message = "Country name must be between 3 and 30 characters")
        String country,

        @NotBlank(message = "City name is required")
        @Size(min = 3, max = 30, message = "City name must be between 3 and 30 characters")
        String city,

        @NotBlank(message = "Street name is required")
        @Size(min = 3, max = 40, message = "Street name must be between 3 and 40 characters")
        String street,

        @NotNull(message = "House number is required")
        @Pattern(
                regexp = "^[1-9][0-9]*[a-zA-Z0-9/\\- ]*$",
                message = "House number must start with a positive integer"
        )
        String number
) {
}
