package pl.edu.medicore.profile.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Gender;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ProfileControllerTest extends AbstractIntegrationTest {
    @Test
    void shouldReturnProfile_whenRequestedByAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/profiles", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("admin@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Adam"))
                .andExpect(jsonPath("$.data.lastName").value("Test"));
    }

    @Test
    void shouldReturnProfile_whenRequestedByDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/profiles", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("rafael.garcia@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.employmentDate").value("2015-06-01"))
                .andExpect(jsonPath("$.data.experience").value(10));
    }

    @Test
    void shouldReturnProfile_whenRequestedByPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/profiles", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.birthDate").value("1990-05-15"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.address.country").value("USA"))
                .andExpect(jsonPath("$.data.address.city").value("New York"))
                .andExpect(jsonPath("$.data.address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.address.number").value(101));
    }

    @Test
    void shouldReturn401_whenRequestedProfileWithInvalidToken() throws Exception {
        mockMvc.perform(get("/profiles")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenRequestedProfileUpdateAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        ProfileUpdateDto dto = new ProfileUpdateDto("TestF", "TestL");

        performRequest(HttpMethod.PUT, "/profiles", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenRequestedProfileUpdateWithInvalidToken() throws Exception {
        ProfileUpdateDto dto = new ProfileUpdateDto("TestF", "TestL");

        mockMvc.perform(put("/profiles", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateDoctorProfile_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        ProfileUpdateDto dto = new ProfileUpdateDto("TestF", "TestL");

        ResultActions resultActions = performRequest(HttpMethod.PUT, "/profiles", dto)
                .andExpect(status().isOk());

        String publicId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        UUID id = UUID.fromString(publicId);

        Doctor doctor = em.createQuery(
                "SELECT a FROM Doctor a WHERE a.publicId = :publicId",
                Doctor.class).setParameter("publicId", id).getSingleResult();

        assertEquals("TestF", doctor.getFirstName());
        assertEquals("TestL", doctor.getLastName());
    }

    @Test
    void shouldReturn400_whenUpdateAdminProfileWithValidationErrors() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ProfileUpdateDto dto = new ProfileUpdateDto(null, "");

        performRequest(HttpMethod.PUT, "/profiles", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(2));
    }

    @Test
    void shouldReturn403_whenRequestedProfileUpdateAsDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);
        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("test", "testL",
                Gender.MALE, 50.7, 100.7, false,
                LocalDate.of(1999, 10, 2), "12345678",
                new PatientAddressDto("test country", "test city", "test street", 10));

        performRequest(HttpMethod.PUT, "/profiles/patient", dto)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401_whenRequestedProfileUpdateForPatientWithInvalidToken() throws Exception {
        ProfileUpdateDto dto = new ProfileUpdateDto("TestF", "TestL");

        mockMvc.perform(put("/profiles/patient", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdatePatientProfile_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("testF", "testL",
                Gender.MALE, 50.7, 100.7, false,
                LocalDate.of(1999, 10, 2), "12345678",
                new PatientAddressDto("test country", "test city", "test street", 10));

        ResultActions resultActions = performRequest(HttpMethod.PUT, "/profiles/patient", dto)
                .andExpect(status().isOk());

        String publicId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        UUID id = UUID.fromString(publicId);

        Patient patient = em.createQuery(
                "SELECT a FROM Patient a WHERE a.publicId = :publicId",
                Patient.class).setParameter("publicId", id).getSingleResult();

        assertEquals("testF", patient.getFirstName());
        assertEquals("testL", patient.getLastName());
        assertEquals("test street", patient.getAddress().getStreet());
    }

    @Test
    void shouldReturn400_whenUpdatePatientProfileWithValidationErrors() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        PatientProfileUpdateDto dto = new PatientProfileUpdateDto("", null,
                Gender.MALE, 50.7, 100.7, false,
                LocalDate.of(1999, 10, 2), "12345678",
                new PatientAddressDto("test country", "test city", "test street", -10));

        performRequest(HttpMethod.PUT, "/profiles/patient", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }
}
