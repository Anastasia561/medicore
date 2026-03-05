package pl.edu.medicore;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.service.AuthService;
import pl.edu.medicore.config.TestContainersConfig;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
public abstract class AbstractControllerIntegrationTest {
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String DOCTOR_EMAIL = "rafael.garcia@example.com";
    private static final String PATIENT_EMAIL = "john.doe@example.com";
    private static final String PASS = "111";

    @Autowired
    private AuthService authService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected ObjectMapper objectMapper;

    private String token;

    protected void obtainRoleBasedToken(Role role) {
        String email = switch (role) {
            case ADMIN -> ADMIN_EMAIL;
            case DOCTOR -> DOCTOR_EMAIL;
            default -> PATIENT_EMAIL;
        };

        AuthRequestDto loginRequest = new AuthRequestDto(email, PASS);
        token = authService.login(loginRequest).accessToken();
    }

    protected ResultActions performRequest(HttpMethod method, String url, Object body, Object... uriVars)
            throws Exception {
        MockHttpServletRequestBuilder requestBuilder;

        if (method == GET) {
            requestBuilder = get(url, uriVars);
        } else if (method == POST) {
            requestBuilder = post(url, uriVars);
        } else if (method == PUT) {
            requestBuilder = put(url, uriVars);
        } else if (method == DELETE) {
            requestBuilder = delete(url, uriVars);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        if (body != null) {
            requestBuilder.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
        }

        return mockMvc.perform(requestBuilder);
    }
}
