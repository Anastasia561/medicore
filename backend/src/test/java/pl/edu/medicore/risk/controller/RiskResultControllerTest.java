package pl.edu.medicore.risk.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.person.model.Role;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RiskResultControllerTest extends AbstractIntegrationTest {
    @Test
    void shouldReturnRisksByPatientId_whenRequestedAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/risks/{patientId}", null, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))

                .andExpect(jsonPath("$.data[0].patientId").value(1))
                .andExpect(jsonPath("$.data[0].disease").value("CKD"))
                .andExpect(jsonPath("$.data[0].riskGroup").value("UNKNOWN"))
                .andExpect(jsonPath("$.data[0].riskPercent").value(0.0))
                .andExpect(jsonPath("$.data[0].testDate").value("2025-01-11"))
                .andExpect(jsonPath("$.data[0].calculatedAt").value("2026-04-19"))

                .andExpect(jsonPath("$.data[1].disease").value("DIABETES"))
                .andExpect(jsonPath("$.data[1].riskGroup").value("UNKNOWN"))
                .andExpect(jsonPath("$.data[1].riskPercent").value(0.0))
                .andExpect(jsonPath("$.data[1].testDate").value("2025-01-11"))
                .andExpect(jsonPath("$.data[1].calculatedAt").value("2026-04-19"))

                .andExpect(jsonPath("$.data[2].disease").value("ANEMIA"))
                .andExpect(jsonPath("$.data[2].riskGroup").value("NONE"))
                .andExpect(jsonPath("$.data[2].riskPercent").value(0.0))
                .andExpect(jsonPath("$.data[2].testDate").value("2025-01-11"))
                .andExpect(jsonPath("$.data[2].calculatedAt").value("2026-04-19"));
    }

    @Test
    void shouldReturn401_whenAccessedRiskResultWithInvalidToken() throws Exception {
        mockMvc.perform(get("/risks/{patientId}", null, 1)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedRecordAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/risks/{patientId}", null, 1)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenPatientNotFound() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/risks/{patientId}", null, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Patient not found"));
    }
}
