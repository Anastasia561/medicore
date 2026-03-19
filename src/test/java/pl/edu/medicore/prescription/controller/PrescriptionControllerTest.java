package pl.edu.medicore.prescription.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractControllerIntegrationTest;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.model.Prescription;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrescriptionControllerTest extends AbstractControllerIntegrationTest {
    @Test
    void shouldReturn403_whenCreatePrescriptionAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreatePrescriptionWithInvalidToken() throws Exception {
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        mockMvc.perform(post("/prescriptions", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "", null,
                LocalDate.of(2026, 12, 3), null, "daily");

        performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(2));
    }

    @Test
    void shouldCreateConsultation_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        ResultActions resultActions = performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Prescription prescription = em.find(Prescription.class, id);
        assertEquals("test medicine", prescription.getMedicine());
        assertEquals("10mg", prescription.getDosage());
        assertNull(prescription.getEndDate());
    }

    @Test
    void shouldReturn400_whenEndTimeBeforeStartTime() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        PrescriptionCreateDto dto = new PrescriptionCreateDto(1L, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), LocalDate.of(2026, 11, 9),
                "daily");

        performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("End date must be after start date"));
    }
}
