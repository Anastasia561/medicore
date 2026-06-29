package pl.edu.medicore.application.profile;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.application.address.dto.AddressDto;
import pl.edu.medicore.application.doctor.Doctor;
import pl.edu.medicore.application.person.Gender;
import pl.edu.medicore.application.person.Role;
import pl.edu.medicore.application.profile.dto.ProfileUpdateDto;

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
                .andExpect(jsonPath("$.data.lastName").value("Test"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.birthDate").value("1990-01-01"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+2345688645"))
                .andExpect(jsonPath("$.data.address.country").value("USA"))
                .andExpect(jsonPath("$.data.address.city").value("New York"))
                .andExpect(jsonPath("$.data.address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.address.number").value("101A"));
    }

    @Test
    void shouldReturnProfile_whenRequestedByDoctor() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        performRequest(HttpMethod.GET, "/profiles", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Rafael"))
                .andExpect(jsonPath("$.data.lastName").value("Garcia"))
                .andExpect(jsonPath("$.data.email").value("rafael.garcia@example.com"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.birthDate").value("1980-01-01"))
                .andExpect(jsonPath("$.data.employmentDate").value("2015-06-01"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+1234567"))
                .andExpect(jsonPath("$.data.specialization").value("CARDIOLOGIST"))
                .andExpect(jsonPath("$.data.address.country").value("USA"))
                .andExpect(jsonPath("$.data.address.city").value("New York"))
                .andExpect(jsonPath("$.data.address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.address.number").value("101A"));
    }

    @Test
    void shouldReturnProfile_whenRequestedByPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/profiles", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.gender").value("MALE"))
                .andExpect(jsonPath("$.data.birthDate").value("1990-05-15"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.address.country").value("USA"))
                .andExpect(jsonPath("$.data.address.city").value("New York"))
                .andExpect(jsonPath("$.data.address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.address.number").value("101A"));
    }

    @Test
    void shouldReturn401_whenRequestedProfileWithInvalidToken() throws Exception {
        mockMvc.perform(get("/profiles")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_whenRequestedProfileUpdateWithInvalidToken() throws Exception {
        ProfileUpdateDto dto = new ProfileUpdateDto("test", "testL",
                Gender.MALE, "1234",
                new AddressDto("test country", "test city", "test street", "10"));

        mockMvc.perform(put("/profiles", dto)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateDoctorProfile_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.DOCTOR);

        ProfileUpdateDto dto = new ProfileUpdateDto("TestF", "TestL",
                Gender.MALE, "123456789",
                new AddressDto("test country", "test city", "test street", "10"));

        ResultActions resultActions = performRequest(HttpMethod.PUT, "/profiles", dto)
                .andExpect(status().isOk());

        String hashId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        long internalId = idObfuscator.decode(hashId);

        Doctor doctor = em.createQuery(
                "SELECT a FROM Doctor a WHERE a.id = :id",
                Doctor.class).setParameter("id", internalId).getSingleResult();

        assertEquals("TestF", doctor.getFirstName());
        assertEquals("TestL", doctor.getLastName());
    }

    @Test
    void shouldReturn400_whenUpdateAdminProfileWithValidationErrors() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        ProfileUpdateDto dto = new ProfileUpdateDto("", null,
                Gender.MALE, "12346789",
                new AddressDto("test country", "test city", "test street", "10"));

        performRequest(HttpMethod.PUT, "/profiles", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(3));
    }
}
