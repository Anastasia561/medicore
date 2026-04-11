package pl.edu.medicore.test.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.person.model.Role;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest extends AbstractIntegrationTest {

    @BeforeEach
    void setupFile() {
        String key = "test/1/report";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("application/pdf")
                        .build(),
                RequestBody.fromBytes("test-content".getBytes())
        );
    }

    @AfterEach
    void cleanupBucket() {
        ListObjectsV2Response list = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .build()
        );

        List<ObjectIdentifier> toDelete = list.contents().stream()
                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                .toList();

        if (!toDelete.isEmpty()) {
            s3Client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build());
        }
    }

    @Test
    void shouldReturnPresignedUrl_whenFileExists() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturn401_whenAccessedTestViewWithInvalidToken() throws Exception {
        mockMvc.perform(get("/tests/view/{id}", null, 1)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedTestViewAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, 1)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenTestNotFound() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("File not found"));
    }

    @Test
    void shouldReturnPresignedUrlForDownload_whenFileExists() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturn401_whenAccessedTestDownloadWithInvalidToken() throws Exception {
        mockMvc.perform(get("/tests/download/{id}", null, 1)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedTestDownloadAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, 1)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenTestNotFoundForDownload() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, 1000)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("File not found"));
    }
}
