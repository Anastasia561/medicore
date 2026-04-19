package pl.edu.medicore.record.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.model.Record;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecordControllerTest extends AbstractIntegrationTest {

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

    @Test
    void shouldReturn401_whenAccessedRecordWithInvalidToken() throws Exception {
        mockMvc.perform(get("/records/appointment/{appointmentId}", null, 6)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedRecordAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/records/appointment/{appointmentId}", null, 6)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenRecordNotFound() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/records/appointment/{appointmentId}", null, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Record not found"));
    }

    @Test
    void shouldReturn403_whenCreateRecordAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        RecordCreateDto dto = new RecordCreateDto(1L, "test diagnosis", "test summary");

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreateConsultationWithInvalidToken() throws Exception {
        RecordCreateDto dto = new RecordCreateDto(1L, "test diagnosis", "test summary");

        mockMvc.perform(post("/records", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        RecordCreateDto dto = new RecordCreateDto(-1L, "", null);

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }

    @Test
    void shouldReturn400_whenAppointmentAlreadyCompleted() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        RecordCreateDto dto = new RecordCreateDto(3L, "test diagnosis", "test summary");

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Appointment is already completed"));
    }

    @Test
    void shouldCreateRecord_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        RecordCreateDto dto = new RecordCreateDto(1L, "test diagnosis", "test summary");

        ResultActions resultActions = performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Record record = em.find(Record.class, id);
        assertEquals("test diagnosis", record.getDiagnosis());
        assertEquals("test summary", record.getSummary());
    }

    @Test
    void shouldReturn403_whenAccessedRecordsAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/records", null)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenGetRecordsWithInvalidToken() throws Exception {
        mockMvc.perform(get("/records")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnPageOfRecords_whenRequestedAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/records", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-01-06"))
                .andExpect(jsonPath("$.data.content[0].doctor.firstName").value("Hannah"))
                .andExpect(jsonPath("$.data.content[0].doctor.lastName").value("Brown"))
                .andExpect(jsonPath("$.data.content[0].doctor.specialization").value("PEDIATRICIAN"))
                .andExpect(jsonPath("$.data.content[1].date").value("2026-02-01"))
                .andExpect(jsonPath("$.data.content[1].doctor.firstName").value("Kevin"))
                .andExpect(jsonPath("$.data.content[1].doctor.lastName").value("Lee"))
                .andExpect(jsonPath("$.data.content[1].doctor.specialization").value("ONCOLOGIST"));
    }

    @Test
    void shouldReturnPageOfRecordsFiltered_whenRequestedAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/records?specialization=ONCOLOGIST", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-02-01"))
                .andExpect(jsonPath("$.data.content[0].doctor.firstName").value("Kevin"))
                .andExpect(jsonPath("$.data.content[0].doctor.lastName").value("Lee"))
                .andExpect(jsonPath("$.data.content[0].doctor.specialization").value("ONCOLOGIST"));
    }

    @Test
    void shouldReturnPageOfRecords_whenRequestedAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/records", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-03-08"))
                .andExpect(jsonPath("$.data.content[0].patient.firstName").value("Anna"))
                .andExpect(jsonPath("$.data.content[0].patient.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.content[0].patient.email").value("anna.smith@example.com"))
                .andExpect(jsonPath("$.data.content[1].date").value("2026-02-06"))
                .andExpect(jsonPath("$.data.content[1].patient.firstName").value("Taro"))
                .andExpect(jsonPath("$.data.content[1].patient.lastName").value("Yamada"))
                .andExpect(jsonPath("$.data.content[1].patient.email").value("taro.yamada@example.com"));
    }

    @Test
    void shouldReturnPageOfRecordsFiltered_whenRequestedAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/records?email=anna.smith", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-03-08"))
                .andExpect(jsonPath("$.data.content[0].patient.firstName").value("Anna"))
                .andExpect(jsonPath("$.data.content[0].patient.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.content[0].patient.email").value("anna.smith@example.com"));
    }
}
