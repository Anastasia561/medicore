package pl.edu.medicore.application.record;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.record.dto.RecordCreateDto;
import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecordControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldReturnRecordById_whenRequestedAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String id = idObfuscator.encode(5L);

        performRequest(HttpMethod.GET, "/records/{id}", null, id)
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
        String id = idObfuscator.encode(6L);
        mockMvc.perform(get("/records/{id}", null, id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedRecordAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        String id = idObfuscator.encode(6L);

        performRequest(HttpMethod.GET, "/records/{id}", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenRecordNotFound() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        String id = idObfuscator.encode(106L);

        performRequest(HttpMethod.GET, "/records/{id}", null, id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("Record not found"));
    }

    @Test
    void shouldReturn403_whenCreateRecordAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        RecordCreateDto dto = new RecordCreateDto(HashId.of(1L), "test diagnosis", "test summary",
                List.of());

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenCreateConsultationWithInvalidToken() throws Exception {
        RecordCreateDto dto = new RecordCreateDto(HashId.of(1L), "test diagnosis", "test summary",
                List.of());

        mockMvc.perform(post("/records", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInCreateDto() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        RecordCreateDto dto = new RecordCreateDto(null, "", null, List.of());

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }

    @Test
    void shouldReturn400_whenAppointmentAlreadyCompleted() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        RecordCreateDto dto = new RecordCreateDto(HashId.of(3L), "test diagnosis", "test summary",
                List.of());

        performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Appointment is already completed"));
    }

    @Test
    void shouldCreateRecord_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        RecordCreateDto dto = new RecordCreateDto(HashId.of(1L), "test diagnosis", "test summary",
                List.of());

        ResultActions resultActions = performRequest(HttpMethod.POST, "/records", dto)
                .andExpect(status().isCreated());

        String hashId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        long internalId = idObfuscator.decode(hashId);

        Record record = em.createQuery(
                "SELECT a FROM Record a WHERE a.id = :id",
                Record.class).setParameter("id", internalId).getSingleResult();

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
                .andExpect(jsonPath("$.data.content[0].date").value("2026-02-01"))
                .andExpect(jsonPath("$.data.content[0].doctor.firstName").value("Kevin"))
                .andExpect(jsonPath("$.data.content[0].doctor.lastName").value("Lee"))
                .andExpect(jsonPath("$.data.content[0].doctor.specialization").value("ONCOLOGIST"))
                .andExpect(jsonPath("$.data.content[1].date").value("2026-01-06"))
                .andExpect(jsonPath("$.data.content[1].doctor.firstName").value("Hannah"))
                .andExpect(jsonPath("$.data.content[1].doctor.lastName").value("Brown"))
                .andExpect(jsonPath("$.data.content[1].doctor.specialization").value("PEDIATRICIAN"));
    }

    @Test
    void shouldReturnPageOfRecords_whenRequestedAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/records", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-04-06"))
                .andExpect(jsonPath("$.data.content[0].patient.firstName").value("Taro"))
                .andExpect(jsonPath("$.data.content[0].patient.lastName").value("Yamada"))
                .andExpect(jsonPath("$.data.content[0].patient.phoneNumber").value("+81123456789"))
                .andExpect(jsonPath("$.data.content[1].date").value("2026-03-08"))
                .andExpect(jsonPath("$.data.content[1].patient.firstName").value("Anna"))
                .andExpect(jsonPath("$.data.content[1].patient.lastName").value("Smith"))
                .andExpect(jsonPath("$.data.content[1].patient.phoneNumber").value("+49123456789"));
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
                .andExpect(jsonPath("$.data.content[0].patient.phoneNumber").value("+49123456789"));
    }
}
