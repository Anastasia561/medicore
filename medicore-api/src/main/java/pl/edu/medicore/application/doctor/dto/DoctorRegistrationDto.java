package pl.edu.medicore.application.doctor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.doctor.Specialization;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.common.validation.annotation.MinAge;
import pl.edu.medicore.common.validation.annotation.Password;
import pl.edu.medicore.common.validation.annotation.UniqueEmail;

import java.time.LocalDate;

public record DoctorRegistrationDto(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @UniqueEmail
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 20, message = "First name must be at most 20 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 20, message = "Last name must be at most 20 characters")
        String lastName,

        @Password
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Repeat password is required")
        String repeatPassword,

        @NotNull(message = "Gender is required")
        Gender gender,

        @NotNull(message = "Experience is required")
        @Positive(message = "Experience must be greater than 0")
        int experience,

        @NotNull(message = "Specialization is required")
        Specialization specialization,

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
        AddressDto address
) {
}
