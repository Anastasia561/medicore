package pl.edu.medicore.consultation.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.model.Consultation;
import pl.edu.medicore.consultation.model.Workday;
import pl.edu.medicore.person.model.Role;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

        performRequest(HttpMethod.GET, "/consultations/doctor/{id}", null, 6)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.data[0].day").value("MONDAY"))
                .andExpect(jsonPath("$.data[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$.data[0].endTime").value("12:00:00"));
    }

    @Test
    void shouldReturn401_whenAccessedConsultationsForDoctorWithInvalidToken() throws Exception {
        mockMvc.perform(get("/consultations/doctor/{id}", null, 6)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenCreateConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        ConsultationCreateDto dto = new ConsultationCreateDto(6L, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreateConsultationWithInvalidToken() throws Exception {
        ConsultationCreateDto dto = new ConsultationCreateDto(6L, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(post("/consultations", dto, 6)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        ConsultationCreateDto dto = new ConsultationCreateDto(-1L, Workday.FRIDAY,
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

        ConsultationCreateDto dto = new ConsultationCreateDto(7L, Workday.THURSDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        ResultActions resultActions = performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Consultation consultation = em.find(Consultation.class, id);
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

        ConsultationCreateDto dto = new ConsultationCreateDto(6L, Workday.FRIDAY,
                LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message").value("Doctor has consultation schedule for selected day"));
    }

    @Test
    void shouldReturn400_whenEndTimeBeforeStartTime() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationCreateDto dto = new ConsultationCreateDto(7L, Workday.THURSDAY,
                LocalTime.of(10, 0), LocalTime.of(9, 0));

        performRequest(HttpMethod.POST, "/consultations", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("End time must be after start time"));
    }

    @Test
    void shouldReturn403_whenUpdateConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, 5)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenUpdateConsultationWithInvalidToken() throws Exception {
        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        mockMvc.perform(put("/consultations/{consultationId}", dto, 5)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInUpdate() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), null);
        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, 5)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldUpdateConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));

        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, 5)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));

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

        ConsultationUpdateDto dto = new ConsultationUpdateDto(LocalTime.of(10, 0), LocalTime.of(12, 0));
        performRequest(HttpMethod.PUT, "/consultations/{consultationId}", dto, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Consultation not found"));
    }

    @Test
    void shouldReturn403_whenDeleteConsultationAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.DELETE, "/consultations/{consultationId}", null, 5)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenDeleteConsultationWithInvalidToken() throws Exception {
        mockMvc.perform(delete("/consultations/{consultationId}", 5)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404_whenConsultationNotFoundForDelete() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.DELETE, "/consultations/{consulationId}", null, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Consultation not found"));
    }

    @Test
    void shouldDeleteConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.DELETE, "/consultations/{consultationId}", null, 1)
                .andExpect(status().isNoContent());

        Consultation consultation = em.find(Consultation.class, 1L);
        assertNull(consultation);

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Schedule Update"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("rafael.garcia@example.com", message.getAllRecipients()[0].toString());
    }
}
