package pl.edu.medicore.profile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.validation.annotation.MinAge;

import java.time.LocalDate;

public record PatientProfileUpdateDto(
        @NotBlank(message = "First name is required")
        @Size(max = 20, message = "First name must be at most 20 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 20, message = "Last name must be at most 20 characters")
        String lastName,

        @NotNull(message = "Gender is required")
        Gender gender,

        @DecimalMin(value = "1.0", message = "Weight must be greater than 0")
        @DecimalMax(value = "500.0", message = "Weight must be less than 500")
        Double weight,

        @DecimalMin(value = "30.0", message = "Height must be greater than 30 cm")
        @DecimalMax(value = "300.0", message = "Height must be less than 300 cm")
        Double height,

        @NotNull(message = "Pregnancy status is required")
        boolean pregnant,

        @MinAge(18)
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "\\+?[0-9]{7,15}",
                message = "Phone number must be valid and contain 7-15 digits"
        )
        String phoneNumber,

        @NotNull(message = "Address is required")
        @Valid
        PatientAddressDto address
) {
}
