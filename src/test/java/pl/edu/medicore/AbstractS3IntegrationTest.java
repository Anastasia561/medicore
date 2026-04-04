package pl.edu.medicore;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import pl.edu.medicore.config.PostgreSQLTestContainersConfig;
import pl.edu.medicore.config.AWSTestConfig;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles({"test", "s3-test"})
@Import({PostgreSQLTestContainersConfig.class, AWSTestConfig.class})
public abstract class AbstractS3IntegrationTest {
    @Autowired
    protected S3Client s3Client;

    @Value("${app.aws.s3.bucket}")
    protected String bucketName;

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

    @BeforeAll
    protected static void init() throws InterruptedException, IOException {
        localStack.execInContainer("awslocal", "s3", "mb", "s3://test-bucket");
    }

    static {
        localStack.start();
    }
}
