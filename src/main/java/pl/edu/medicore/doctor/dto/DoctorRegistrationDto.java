package pl.edu.medicore.doctor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.validation.annotation.Password;
import pl.edu.medicore.validation.annotation.UniqueEmail;

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

        @NotNull(message = "Experience is required")
        @Positive(message = "Experience must be greater than 0")
        int experience,

        @NotNull(message = "Specialization is required")
        Specialization specialization
) {
}
