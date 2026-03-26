package pl.edu.medicore.auth.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import pl.edu.medicore.AbstractControllerIntegrationTest;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.dto.PasswordResetDto;
import pl.edu.medicore.auth.dto.PasswordResetRequestDto;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldLoginSuccessfully_whenCredentialsAreValid() throws Exception {
        AuthRequestDto dto = new AuthRequestDto("admin@example.com", "111");

        performRequest(HttpMethod.POST, "/auth/login", dto)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE));
    }

    @Test
    void shouldReturn401_whenCredentialsDoNotExist() throws Exception {
        AuthRequestDto dto = new AuthRequestDto("test@example.com", "111");

        performRequest(HttpMethod.POST, "/auth/login", dto)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenCredentialsAreNotValid() throws Exception {
        AuthRequestDto dto = new AuthRequestDto("test@gmail.com", null);

        performRequest(HttpMethod.POST, "/auth/login", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldRefreshToken_whenRefreshCookieIsPresent() throws Exception {
        AuthRequestDto loginDto = new AuthRequestDto("admin@example.com", "111");

        Cookie refreshCookie = performRequest(HttpMethod.POST, "/auth/login", loginDto)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookie("refreshToken");

        assertThat(refreshCookie).isNotNull();

        mockMvc.perform(post("/auth/refresh").cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorized_whenRefreshCookieIsMissing() throws Exception {
        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldLogoutSuccessfully_whenRefreshCookieIsPresent() throws Exception {
        AuthRequestDto loginDto = new AuthRequestDto("admin@example.com", "111");

        MvcResult loginResponse = performRequest(HttpMethod.POST, "/auth/login", loginDto)
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(loginResponse.getResponse().getContentAsString(), "$.accessToken");
        Cookie refreshCookie = loginResponse.getResponse().getCookie("refreshToken");

        assertThat(refreshCookie).isNotNull();

        mockMvc.perform(post("/auth/logout")
                        .cookie(refreshCookie)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400_whenValidationErrorsForPasswordResetRequest() throws Exception {
        PasswordResetRequestDto dto = new PasswordResetRequestDto(null);

        performRequest(HttpMethod.POST, "/auth/reset-password/request", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldRequestPasswordResetSuccessfully_whenInputIsValid() throws Exception {
        PasswordResetRequestDto dto = new PasswordResetRequestDto("john.doe@example.com");

        performRequest(HttpMethod.POST, "/auth/reset-password/request", dto)
                .andExpect(status().isNoContent());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Password reset request"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("john.doe@example.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn400_whenResetPasswordRequestTooFrequent() throws Exception {
        PasswordResetRequestDto dto = new PasswordResetRequestDto("john.doe@example.com");

        performRequest(HttpMethod.POST, "/auth/reset-password/request", dto)
                .andExpect(status().isNoContent());

        performRequest(HttpMethod.POST, "/auth/reset-password/request", dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_whenValidationErrorsForPasswordReset() throws Exception {
        PasswordResetDto dto = new PasswordResetDto("", "john.doe@example.com",
                "StrongPass123!", null);

        performRequest(HttpMethod.POST, "/auth/reset-password", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(2));
    }

    @Test
    void shouldReturn400_whenPasswordsDoNotMatchForReset() throws Exception {
        insertVerificationToken();

        PasswordResetDto dto = new PasswordResetDto("token", "john.doe@example.com",
                "StrongPass123!", "StrongPass");

        performRequest(HttpMethod.POST, "/auth/reset-password", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Passwords don't match"));
    }

    @Test
    void shouldResetPassword_whenInputIsValid() throws Exception {
        insertVerificationToken();

        PasswordResetDto dto = new PasswordResetDto("token", "john.doe@example.com",
                "StrongPass123!", "StrongPass123!");


        performRequest(HttpMethod.POST, "/auth/reset-password", dto)
                .andExpect(status().isNoContent());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Password Reset Confirmation"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("john.doe@example.com", message.getAllRecipients()[0].toString());
    }

    private void insertVerificationToken() {
        String tokenHash = passwordEncoder.encode("token");
        VerificationToken token = new VerificationToken();
        token.setTokenHash(tokenHash);
        token.setTokenType(TokenType.PASSWORD_RESET);
        token.setEmail("john.doe@example.com");
        token.setExpiresAt(Instant.now().plusSeconds(300));
        em.persist(token);
        em.flush();
        em.clear();
    }
}
