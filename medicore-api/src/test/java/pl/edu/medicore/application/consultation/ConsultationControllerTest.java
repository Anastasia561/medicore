package pl.edu.medicore.application.consultation;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.application.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.application.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConsultationControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAllDoctorConsultations_whenConsultationsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String id = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/consultations/doctor/{id}", null, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].day").value("MONDAY"))
                .andExpect(jsonPath("$.data[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$.data[0].endTime").value("12:00:00"));
    }

    @Test
    void shouldReturn401_whenAccessedConsultationsWithInvalidToken() throws Exception {
        String id = idObfuscator.encode(6L);
        mockMvc.perform(get("/consultations/doctor/{id}", null, id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedConsultationsAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        String doctorId = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/consultations/doctor/{id}", null, doctorId)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenDoctorNotFoundForConsultation() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String doctorId = idObfuscator.encode(105L);

        performRequest(HttpMethod.GET, "/consultations/doctor/{id}", null, doctorId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Doctor not found"));
    }

    @Test
    void shouldGetAllDoctorConsultations_whenConsultationsExistForDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/consultations", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].day").value("MONDAY"))
                .andExpect(jsonPath("$.data[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$.data[0].endTime").value("12:00:00"));
    }

    @Test
    void shouldReturn401_whenAccessedConsultationsForDoctorWithInvalidToken() throws Exception {
        mockMvc.perform(get("/consultations}")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedConsultationsForDoctorAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/consultations", null)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403_whenCreateConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        ConsultationCreateDto dto = new ConsultationCreateDto(HashId.of(6L), Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreateConsultationWithInvalidToken() throws Exception {
        ConsultationCreateDto dto = new ConsultationCreateDto(HashId.of(6L), Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(post("/consultations", dto, 6)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        ConsultationCreateDto dto = new ConsultationCreateDto(null, Workday.FRIDAY,
                LocalTime.of(10, 0), null);

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(2));
    }

    @Test
    void shouldCreateConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationCreateDto dto = new ConsultationCreateDto(HashId.of(7L), Workday.THURSDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        ResultActions resultActions = performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isCreated());

        String hashId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        Long internalId = idObfuscator.decode(hashId);

        Consultation consultation = em.createQuery(
                "SELECT a FROM Consultation a WHERE a.id = :id",
                Consultation.class).setParameter("id", internalId).getSingleResult();

        assertEquals(Workday.THURSDAY, consultation.getWorkday());
        assertEquals(LocalTime.of(10, 0), consultation.getStartTime());
        assertEquals(LocalTime.of(12, 0), consultation.getEndTime());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Schedule Update"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("laura.johnson@example.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn409_whenConsultationExistsForProvidedDay() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationCreateDto dto = new ConsultationCreateDto(HashId.of(6L), Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message")
                        .value("Doctor has consultation schedule for selected day"));
    }

    @Test
    void shouldReturn400_whenEndTimeBeforeStartTime() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationCreateDto dto = new ConsultationCreateDto(HashId.of(7L), Workday.THURSDAY,
                LocalTime.of(10, 0), LocalTime.of(9, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("End startTime must be after start startTime"));
    }

    @Test
    void shouldReturn403_whenUpdateConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String consultationId = idObfuscator.encode(5L);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, consultationId)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenUpdateConsultationWithInvalidToken() throws Exception {
        String consultationId = idObfuscator.encode(5L);
        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(put("/consultations/{consultationId}", dto, consultationId)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInUpdate() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String consultationId = idObfuscator.encode(5L);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), null);
        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, consultationId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldUpdateConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String consultationId = idObfuscator.encode(5L);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, consultationId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        Consultation consultation = em.find(Consultation.class, 5L);
        assertEquals(LocalTime.of(10, 0), consultation.getStartTime());
        assertEquals(LocalTime.of(12, 0), consultation.getEndTime());

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Schedule Update"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("rafael.garcia@example.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn404_whenConsultationNotFoundForUpdate() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String consultationId = idObfuscator.encode(105L);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));
        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, consultationId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Consultation not found"));
    }

    @Test
    void shouldReturn403_whenDeleteConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String consultationId = idObfuscator.encode(5L);

        performRequest(HttpMethod.DELETE, "/consultations/{consultationId}", null, consultationId)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenDeleteConsultationWithInvalidToken() throws Exception {
        String consultationId = idObfuscator.encode(5L);
        mockMvc.perform(delete("/consultations/{consultationId}", consultationId)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404_whenConsultationNotFoundForDelete() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String consultationId = idObfuscator.encode(105L);

        performRequest(HttpMethod.DELETE, "/consultations/{consultationId}", null, consultationId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Consultation not found"));
    }

    @Test
    void shouldDeleteConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String consultationId = idObfuscator.encode(5L);
        Long internalId = idObfuscator.decode(consultationId);

        performRequest(HttpMethod.DELETE, "/consultations/{consultationId}", null, consultationId)
                .andExpect(status().isNoContent());

        List<Consultation> result = em.createQuery(
                        "SELECT a FROM Consultation a WHERE a.id = :id",
                        Consultation.class)
                .setParameter("id", internalId)
                .getResultList();

        assertTrue(result.isEmpty());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Schedule Update"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("rafael.garcia@example.com", message.getAllRecipients()[0].toString());
    }
}
