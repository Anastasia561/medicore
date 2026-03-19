package pl.edu.medicore.statistics.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import pl.edu.medicore.AbstractControllerIntegrationTest;
import pl.edu.medicore.person.model.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class StatisticsControllerTest extends AbstractControllerIntegrationTest {
    @Test
    void shouldReturnAdminStatistics_wheRecordsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/statistics/admin", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPatients").value(5))
                .andExpect(jsonPath("$.data.totalDoctors").value(5))
                .andExpect(jsonPath("$.data.consultationsToday").value(0))
                .andExpect(jsonPath("$.data.monthlyConsultations").isArray())
                .andExpect(jsonPath("$.data.monthlyConsultations.length()").value(10))
                .andExpect(jsonPath("$.data.doctorsBySpecialization").isArray())
                .andExpect(jsonPath("$.data.doctorsBySpecialization.length()").value(5));
    }

    @Test
    void shouldReturnDoctorStatistics_wheRecordsExist() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/statistics/doctor/{id}", null, 6)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPatients").value(3))
                .andExpect(jsonPath("$.data.consultationsToday").value(0))
                .andExpect(jsonPath("$.data.monthlyConsultations").isArray())
                .andExpect(jsonPath("$.data.monthlyConsultations.length()").value(5));
    }

    @Test
    void shouldReturn403_whenAccessedDoctorStatisticsAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/statistics/doctor/{id}", null, 6)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403_whenAccessedAdminStatisticsAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/statistics/admin", null, 6)
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
        mockMvc.perform(get("/statistics/doctor/{id}", 1L)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
