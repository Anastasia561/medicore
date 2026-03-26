package pl.edu.medicore.patient.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractControllerIntegrationTest;
import pl.edu.medicore.address.dto.PatientAddressDto;
import pl.edu.medicore.address.model.Address;
import pl.edu.medicore.city.model.City;
import pl.edu.medicore.coutry.model.Country;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientVerificationRequestDto;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.person.model.Role;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PatientControllerTest extends AbstractControllerIntegrationTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturn401_whenAccessedPatientListingWithInvalidToken() throws Exception {
        mockMvc.perform(get("/patients")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedPatientListingAsPatient() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/patients", null)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnPageOfPatients_whenPatientsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/patients", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(5))

                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.content[0].phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.content[0].birthDate").value("1990-05-15"))

                .andExpect(jsonPath("$.data.content[0].address.country").value("USA"))
                .andExpect(jsonPath("$.data.content[0].address.city").value("New York"))
                .andExpect(jsonPath("$.data.content[0].address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.content[0].address.number").value(101));
    }

    @Test
    void shouldReturnPageOfPatientsWithEmailSearch_whenPatientsExist() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/patients?search=john.doe", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))

                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.content[0].phoneNumber").value("+123456789"))
                .andExpect(jsonPath("$.data.content[0].birthDate").value("1990-05-15"))

                .andExpect(jsonPath("$.data.content[0].address.country").value("USA"))
                .andExpect(jsonPath("$.data.content[0].address.city").value("New York"))
                .andExpect(jsonPath("$.data.content[0].address.street").value("5th Avenue"))
                .andExpect(jsonPath("$.data.content[0].address.number").value(101));
    }

    @Test
    void shouldReturn400_whenValidationErrorsInRegisterPatient() throws Exception {
        PatientAddressDto addressDto = new PatientAddressDto(null, "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", null, "Doe",
                "pass", "pass", LocalDate.of(2010, 7, 2), "123", addressDto);


        performRequest(HttpMethod.POST, "/patients/register", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(6));
    }

    @Test
    void shouldReturn400_whenPasswordsDoNotMatchForPatientRegistration() throws Exception {
        PatientAddressDto addressDto = new PatientAddressDto("Poland", "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "John", "Doe",
                "StrongPass1235!", "StrongPass123!", LocalDate.of(2003, 7, 2),
                "123456789", addressDto);


        performRequest(HttpMethod.POST, "/patients/register", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Passwords don't match"));
    }

    @Test
    void shouldRegisterPatient_whenInputIsValid() throws Exception {
        PatientAddressDto addressDto = new PatientAddressDto("Poland", "Warsaw", "Street", 10);
        PatientRegisterDto dto = new PatientRegisterDto("test@gmail.com", "TestF", "TestL",
                "StrongPass123!", "StrongPass123!", LocalDate.of(2003, 7, 2),
                "123456789", addressDto);


        ResultActions resultActions = performRequest(HttpMethod.POST, "/patients/register", dto)
                .andExpect(status().isCreated());

        Long id = ((Number) JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        )).longValue();

        Patient patient = em.find(Patient.class, id);
        assertEquals("TestF", patient.getFirstName());
        assertEquals("TestL", patient.getLastName());
        assertEquals(Status.UNVERIFIED, patient.getStatus());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Email Verification"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("test@gmail.com", message.getAllRecipients()[0].toString());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInEmailVerification() throws Exception {
        PatientVerificationRequestDto dto = new PatientVerificationRequestDto("test@gmail.com", null);

        performRequest(HttpMethod.POST, "/patients/verify-email", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }

    @Test
    void shouldReturn400_whenTokenIsInvalidForEmailVerification() throws Exception {
        PatientVerificationRequestDto dto = new PatientVerificationRequestDto("test@gmail.com", "token");

        performRequest(HttpMethod.POST, "/patients/verify-email", dto)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid or expired token"));
    }

    @Test
    void shouldSuccessfullyValidatePatientEmail_whenInputIsValid() throws Exception {
        insertUnverifiedPatientAndVerificationToken();

        PatientVerificationRequestDto dto = new PatientVerificationRequestDto("test@gmail.com", "token");

        performRequest(HttpMethod.POST, "/patients/verify-email", dto)
                .andExpect(status().isOk());

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        MimeMessage message = messages[0];

        assertEquals(1, messages.length);
        assertTrue(message.getSubject().contains("Registration Confirmation"));
        assertEquals(1, message.getAllRecipients().length);
        assertEquals("test@gmail.com", message.getAllRecipients()[0].toString());
    }

    private void insertUnverifiedPatientAndVerificationToken(){
        String tokenHash = passwordEncoder.encode("token");
        VerificationToken token = new VerificationToken();
        token.setTokenHash(tokenHash);
        token.setTokenType(TokenType.EMAIL_VERIFICATION);
        token.setEmail("test@gmail.com");
        token.setExpiresAt(Instant.now().plusSeconds(300));
        em.persist(token);

        Country country = new Country();
        country.setName("United States");

        City city = new City();
        city.setName("New York");
        city.setCountry(country);

        Address address = new Address();
        address.setStreet("5th Avenue");
        address.setNumber(101);
        address.setCity(city);

        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("test@gmail.com");
        patient.setPassword("encoded_password");
        patient.setRole(Role.PATIENT);
        patient.setStatus(Status.UNVERIFIED);

        patient.setBirthDate(LocalDate.of(1990, 5, 15));
        patient.setPhoneNumber("+123456789");
        patient.setAddress(address);

        em.persist(patient);
        em.flush();
        em.clear();
    }
}
