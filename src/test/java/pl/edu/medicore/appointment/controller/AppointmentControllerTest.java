package pl.edu.medicore.appointment.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.person.model.Role;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAppointmentPageForPatient_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/appointments?userId=1&startDate=2026-01-10&endDate=2026-03-10", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-03-05"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data.content[0].phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.content[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.content[0].time").value("11:00:00"));
    }

    @Test
    void shouldGetAppointmentPageForDoctor_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/appointments?userId=6&startDate=2026-01-10&endDate=2026-03-10", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(4))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-03-05"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.content[0].specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.content[0].status").value("CANCELLED"))
                .andExpect(jsonPath("$.data.content[0].time").value("11:00:00"));
    }

    @Test
    void shouldGetAppointmentPageForPatientWithStatusFiltering_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/appointments?userId=1&startDate=2026-01-10&endDate=2026-03-10&status=SCHEDULED", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-02-06"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data.content[0].phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.content[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$.data.content[0].time").value("12:00:00"));
    }

    @Test
    void shouldReturn401_whenAccessedAppointmentsWithInvalidToken() throws Exception {
        mockMvc.perform(get("/appointments?userId=1&startDate=2026-01-10&endDate=2026-03-10")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenRequiredParamIsNotPresent() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/appointments?userId=1&startDate=2026-01-10", null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldReturn400_whenGetAppointmentsWithInvalidDateRange() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/appointments?userId=1&startDate=2026-03-10&endDate=2026-01-10", null, 7)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("End date must be after start date"));
    }

    @Test
    void shouldReturn401_whenCancelAppointmentWithInvalidToken() throws Exception {
        mockMvc.perform(put("/appointments/cancel/{id}", 1)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn409_whenCancelCompletedAppointment() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.PUT, "/appointments/cancel/{id}", null, 3)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message").value("Appointment can not be cancelled"));
    }

    @Test
    void shouldReturn409_whenCancelCancelledAppointment() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.PUT, "/appointments/cancel/{id}", null, 4)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.message").value("Appointment can not be cancelled"));
    }

    @Test
    void shouldReturn404_whenCancelCancelledAppointment() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.PUT, "/appointments/cancel/{id}", null, 40)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Appointment not found"));
    }

    @Test
    void shouldCancelAppointment_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.PUT, "/appointments/cancel/{id}", null, 1)
                .andExpect(status().isOk());

        greenMail.waitForIncomingEmail(2);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage patientEmail = messages[0];

        assertEquals(2, messages.length);
        assertTrue(patientEmail.getSubject().contains("Appointment Cancellation"));
    }

    @Test
    void shouldReturn403_whenCreatingAppointmentAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        AppointmentCreateDto dto = new AppointmentCreateDto(6L,
                LocalDate.of(2026, 10, 10), LocalTime.of(10, 30));

        performRequest(HttpMethod.POST, "/appointments", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenAccessedAppointmentCreationWithInvalidToken() throws Exception {
        AppointmentCreateDto dto = new AppointmentCreateDto(6L,
                LocalDate.of(2026, 10, 10), LocalTime.of(10, 30));

        mockMvc.perform(post("/appointments", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInAppointmentCreation() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        AppointmentCreateDto dto = new AppointmentCreateDto(-6L,
                LocalDate.of(2024, 10, 10), null);

        performRequest(HttpMethod.POST, "/appointments", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }

    @Test
    void shouldReturn400_whenTimeSlotIsInvalid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        AppointmentCreateDto dto = new AppointmentCreateDto(6L,
                LocalDate.of(2026, 11, 4), LocalTime.of(10, 30));

        performRequest(HttpMethod.POST, "/appointments", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Selected time slot is not available"));
    }

    @Test
    void shouldSuccessfullyRegisterDoctor_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        AppointmentCreateDto dto = new AppointmentCreateDto(6L,
                LocalDate.of(2028, 4, 6), LocalTime.of(10, 30));

        ResultActions resultActions = performRequest(HttpMethod.POST, "/appointments", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Appointment appointment = em.find(Appointment.class, id);
        assertEquals("John", appointment.getPatient().getFirstName());
        assertEquals("Garcia", appointment.getDoctor().getLastName());
        assertEquals(Status.SCHEDULED, appointment.getStatus());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Appointment Scheduled"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("john.doe@example.com", message.getAllRecipients()[0].toString());
    }
}
