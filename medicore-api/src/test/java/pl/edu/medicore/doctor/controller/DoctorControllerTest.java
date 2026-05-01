package pl.edu.medicore.doctor.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.doctor.model.Specialization;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DoctorControllerTest extends AbstractIntegrationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldGetAllAvailableTimesForDoctor_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000006");

        performRequest(HttpMethod.GET, "/doctors/{id}/times?date=2026-03-04", null, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0]").value("10:00:00"))
                .andExpect(jsonPath("$.data[1]").value("11:00:00"))
                .andExpect(jsonPath("$.data[2]").value("12:00:00"))
                .andExpect(jsonPath("$.data[3]").value("13:00:00"));
    }

    @Test
    void shouldReturn401_whenAccessedDoctorAvailableTimesWithInvalidToken() throws Exception {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000006");
        mockMvc.perform(get("/doctors/{id}/times?date=2026-03-04", id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenGetDoctorAvailableTimesAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000006");

        performRequest(HttpMethod.GET, "/doctors/{id}/times?date=2026-03-04", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenDoctorDoesNotExistsForAvailableTimes() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

        performRequest(HttpMethod.GET, "/doctors/{id}/times?date=2026-03-04", null, id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Doctor not found"));
    }

    @Test
    void shouldReturn400_whenDoctorIsNotAvailableOnWeekends() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000006");

        performRequest(HttpMethod.GET, "/doctors/{id}/times?date=2026-03-01", null, id)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Doctor is not available on weekends"));
    }

    @Test
    void shouldReturn400_whenDoctorIsNotAvailable() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000007");

        performRequest(HttpMethod.GET, "/doctors/{id}/times?date=2026-03-03", null, id)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Doctor is not available"));
    }

    @Test
    void shouldReturn401_whenAccessedDoctorListingWithInvalidToken() throws Exception {
        mockMvc.perform(get("/doctors")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedDoctorListingAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/doctors", null)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnPageOfDoctors_whenDoctorsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/doctors", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(5))

                .andExpect(jsonPath("$.data.content[0].firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.content[0].email").value("rafael.garcia@example.com"))
                .andExpect(jsonPath("$.data.content[0].specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.content[0].employmentDate").value("2015-06-01"))
                .andExpect(jsonPath("$.data.content[0].experience").value(10));
    }

    @Test
    void shouldReturnPageOfDoctorsWithEmailSearch_whenDoctorExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/doctors?query=rafael.garcia", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))

                .andExpect(jsonPath("$.data.content[0].firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.content[0].email").value("rafael.garcia@example.com"))
                .andExpect(jsonPath("$.data.content[0].specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.content[0].employmentDate").value("2015-06-01"))
                .andExpect(jsonPath("$.data.content[0].experience").value(10));
    }

    @Test
    void shouldReturnPageOfDoctorsWithSpecializationSearch_whenDoctorExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/doctors?specialization=CARDIOLOGIST", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))

                .andExpect(jsonPath("$.data.content[0].firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.content[0].email").value("rafael.garcia@example.com"))
                .andExpect(jsonPath("$.data.content[0].specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.content[0].employmentDate").value("2015-06-01"))
                .andExpect(jsonPath("$.data.content[0].experience").value(10));
    }

    @Test
    void shouldReturn400_whenValidationErrorsInDoctorInvitation() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        DoctorInvitationRequestDto dto = new DoctorInvitationRequestDto("", null, "TestL");
        performRequest(HttpMethod.POST, "/doctors/invite", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(2));
    }

    @Test
    void shouldReturn403_whenAccessedDoctorInvitationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        DoctorInvitationRequestDto dto = new DoctorInvitationRequestDto("test@gmail.com", "TestF", "TestL");

        performRequest(HttpMethod.POST, "/doctors/invite", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenAccessedDoctorInvitationWithInvalidToken() throws Exception {
        DoctorInvitationRequestDto dto = new DoctorInvitationRequestDto("test@gmail.com", "TestF", "TestL");

        mockMvc.perform(get("/doctors/invite", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldInviteDoctor_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        DoctorInvitationRequestDto dto = new DoctorInvitationRequestDto("test@gmail.com", "TestF", "TestL");

        performRequest(HttpMethod.POST, "/doctors/invite", dto)
                .andExpect(status().isNoContent());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Doctor Registration Invitation"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("test@gmail.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInDoctorRegistration() throws Exception {
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token", "", null,
                "TestL", "StrongPass123!", "StrongPass123!", null,
                10, Specialization.CARDIOLOGIST);

        performRequest(HttpMethod.POST, "/doctors/register", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }

    @Test
    void shouldReturn400_whenTokenIsInvalidForDoctorRegistration() throws Exception {
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token123", "test@gmail.com", "TestF",
                "TestL", "StrongPass123!", "StrongPass123!", Gender.FEMALE,
                10, Specialization.CARDIOLOGIST);

        performRequest(HttpMethod.POST, "/doctors/register", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid or expired token"));
    }

    @Test
    void shouldSuccessfullyRegisterDoctor_whenInputIsValid() throws Exception {
        insertVerificationToken();

        DoctorRegistrationDto dto = new DoctorRegistrationDto("token", "test@gmail.com", "TestF",
                "TestL", "StrongPass123!", "StrongPass123!", Gender.FEMALE,
                10, Specialization.CARDIOLOGIST);

        ResultActions resultActions = performRequest(HttpMethod.POST, "/doctors/register", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Doctor doctor = em.find(Doctor.class, id);
        assertEquals("TestF", doctor.getFirstName());
        assertEquals("TestL", doctor.getLastName());
        assertEquals(Status.ACTIVE, doctor.getStatus());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Registration Confirmation"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("test@gmail.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn400_whenPasswordsDoNotMatchForDoctorRegistration() throws Exception {
        insertVerificationToken();
        DoctorRegistrationDto dto = new DoctorRegistrationDto("token", "test@gmail.com", "TestF",
                "TestL", "StrongPass123!", "StrongPass", Gender.FEMALE,
                10, Specialization.CARDIOLOGIST);

        performRequest(HttpMethod.POST, "/doctors/register", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Passwords don't match"));
    }

    private void insertVerificationToken() {
        String tokenHash = passwordEncoder.encode("token");
        VerificationToken token = new VerificationToken();
        token.setTokenHash(tokenHash);
        token.setTokenType(TokenType.DOCTOR_INVITATION);
        token.setEmail("test@gmail.com");
        token.setExpiresAt(Instant.now().plusSeconds(300));
        em.persist(token);
    }
}
