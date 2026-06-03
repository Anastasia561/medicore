package pl.edu.medicore.application.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.application.person.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class StatisticsControllerTest extends AbstractIntegrationTest {
    @Test
    void shouldReturnAdminStatistics_wheRecordsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/statistics/admin", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPatients").value(5))
                .andExpect(jsonPath("$.data.totalDoctors").value(5))
                .andExpect(jsonPath("$.data.consultationsToday").value(0))
                .andExpect(jsonPath("$.data.monthlyConsultations").isArray())
                .andExpect(jsonPath("$.data.monthlyConsultations.length()").value(11))
                .andExpect(jsonPath("$.data.doctorsBySpecialization").isArray())
                .andExpect(jsonPath("$.data.doctorsBySpecialization.length()").value(5));
    }

    @Test
    void shouldReturnDoctorStatistics_wheRecordsExist() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        String id = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/statistics/doctor/{id}", null, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPatients").value(3))
                .andExpect(jsonPath("$.data.consultationsToday").value(0))
                .andExpect(jsonPath("$.data.monthlyConsultations").isArray())
                .andExpect(jsonPath("$.data.monthlyConsultations.length()").value(6));
    }

    @Test
    void shouldReturn403_whenAccessedDoctorStatisticsAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String id = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/statistics/doctor/{id}", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403_whenAccessedAdminStatisticsAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String id = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/statistics/admin", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenAccessedAdminStatisticsWithInvalidToken() throws Exception {
        mockMvc.perform(get("/statistics/admin")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenAccessedDoctorStatisticsWithInvalidToken() throws Exception {
        String id = idObfuscator.encode(6L);

        mockMvc.perform(get("/statistics/doctor/{id}", id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
