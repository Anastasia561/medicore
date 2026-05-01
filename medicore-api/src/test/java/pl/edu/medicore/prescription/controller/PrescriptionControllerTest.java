package pl.edu.medicore.prescription.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.prescription.dto.PrescriptionCreateDto;
import pl.edu.medicore.prescription.model.Prescription;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrescriptionControllerTest extends AbstractIntegrationTest {
    @Test
    void shouldReturn403_whenCreatePrescriptionAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("20000000-0000-0000-0000-000000000001");

        PrescriptionCreateDto dto = new PrescriptionCreateDto(id, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreatePrescriptionWithInvalidToken() throws Exception {
        UUID id = UUID.fromString("20000000-0000-0000-0000-000000000001");
        PrescriptionCreateDto dto = new PrescriptionCreateDto(id, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        mockMvc.perform(post("/prescriptions", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        UUID id = UUID.fromString("20000000-0000-0000-0000-000000000001");

        PrescriptionCreateDto dto = new PrescriptionCreateDto(id, "", null,
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
        UUID prescriptionId = UUID.fromString("20000000-0000-0000-0000-000000000001");

        PrescriptionCreateDto dto = new PrescriptionCreateDto(prescriptionId, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), null, "daily");

        ResultActions resultActions = performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isCreated());

        String publicId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        UUID id = UUID.fromString(publicId);

        Prescription prescription = em.createQuery(
                "SELECT a FROM Prescription a WHERE a.publicId = :publicId",
                Prescription.class).setParameter("publicId", id).getSingleResult();

        assertEquals("test medicine", prescription.getMedicine());
        assertEquals("10mg", prescription.getDosage());
        assertNull(prescription.getEndDate());
    }

    @Test
    void shouldReturn400_whenEndTimeBeforeStartTime() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        UUID id = UUID.fromString("20000000-0000-0000-0000-000000000001");

        PrescriptionCreateDto dto = new PrescriptionCreateDto(id, "test medicine", "10mg",
                LocalDate.of(2026, 12, 3), LocalDate.of(2026, 11, 9),
                "daily");

        performRequest(HttpMethod.POST, "/prescriptions", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("End date must be after start date"));
    }
}
