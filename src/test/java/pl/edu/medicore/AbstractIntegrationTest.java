package pl.edu.medicore;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.service.AuthService;
import pl.edu.medicore.config.AWSTestConfig;
import pl.edu.medicore.config.PostgreSQLTestContainersConfig;
import pl.edu.medicore.person.model.Role;
import software.amazon.awssdk.services.s3.S3Client;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

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
@Import({PostgreSQLTestContainersConfig.class, AWSTestConfig.class})
public abstract class AbstractIntegrationTest {
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String DOCTOR_EMAIL = "rafael.garcia@example.com";
    private static final String PATIENT_EMAIL = "john.doe@example.com";
    private static final String PASS = "111";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected S3Client s3Client;

    @Value("${app.aws.s3.bucket}")
    protected String bucketName;

    @RegisterExtension
    protected static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withPerMethodLifecycle(true);

    protected String token;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    protected static void init() throws InterruptedException, IOException {
        localStack.execInContainer("awslocal", "s3", "mb", "s3://test-bucket");
    }

    private static final LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
                    .withServices(LocalStackContainer.Service.S3);

    @DynamicPropertySource
    private static void registerAwsProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.s3.endpoint",
                () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
    }

    static {
        localStack.start();
    }

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
