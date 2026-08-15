package pl.edu.medicore.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pl.edu.medicore.common.validation.annotation.Password;

public record PasswordResetDto(
        @NotBlank(message = "Token is required")
        String token,

        @Password
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Repeat password is required")
        String repeatPassword
) {
}
