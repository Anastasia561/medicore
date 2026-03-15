package pl.edu.medicore.record.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import pl.edu.medicore.AbstractControllerIntegrationTest;
import pl.edu.medicore.person.model.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecordControllerTest extends AbstractControllerIntegrationTest {

    @Test
    void shouldReturnRecordByAppointmentId_whenRequestedAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/records/appointment/{appointmentId}", null, 12)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.doctor.firstName").value("Kevin"))
                .andExpect(jsonPath("$.data.doctor.lastName").value("Lee"))
                .andExpect(jsonPath("$.data.doctor.specialization").value("ONCOLOGIST"))
                .andExpect(jsonPath("$.data.patient.firstName").value("John"))
                .andExpect(jsonPath("$.data.patient.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.patient.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.date").value("2026-02-01"))
                .andExpect(jsonPath("$.data.diagnosis").value("High cholesterol"))
                .andExpect(jsonPath("$.data.summary").value("Elevated LDL levels"))
                .andExpect(jsonPath("$.data.prescriptions").isArray())
                .andExpect(jsonPath("$.data.prescriptions.length()").value(0));
    }
}
