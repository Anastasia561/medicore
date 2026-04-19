package pl.edu.medicore.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.validation.annotation.Password;

public record PasswordResetDto(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Password
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Repeat password is required")
        String repeatPassword
) {
}
