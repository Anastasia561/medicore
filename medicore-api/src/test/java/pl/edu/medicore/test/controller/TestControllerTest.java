package pl.edu.medicore.test.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.person.model.Role;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest extends AbstractIntegrationTest {

    @BeforeEach
    void setupFile() {
        String key = "test/11100000-0000-0000-0000-000000000000/report";

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
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturn401_whenAccessedTestViewWithInvalidToken() throws Exception {
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        mockMvc.perform(get("/tests/view/{id}", null, id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedTestViewAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenTestNotFound() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000001");

        performRequest(HttpMethod.GET, "/tests/view/{id}", null, id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("File not found"));
    }

    @Test
    void shouldReturnPresignedUrlForDownload_whenFileExists() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void shouldReturn401_whenAccessedTestDownloadWithInvalidToken() throws Exception {
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        mockMvc.perform(get("/tests/download/{id}", null, id)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedTestDownloadAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000000");

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, id)
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404_whenTestNotFoundForDownload() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);
        UUID id = UUID.fromString("11100000-0000-0000-0000-000000000001");

        performRequest(HttpMethod.GET, "/tests/download/{id}", null, id)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.message").value("File not found"));
    }

    @Test
    void shouldCreateTest_whenInputIsValid() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        ResultActions resultActions = mockMvc.perform(
                        multipart("/tests")
                                .file(file)
                                .param("date", "2026-01-10")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated());

        String publicId = JsonPath.read(
                resultActions.andReturn().getResponse().getContentAsString(),
                "$.data"
        );

        UUID id = UUID.fromString(publicId);

        pl.edu.medicore.test.model.Test test = em.createQuery(
                "SELECT a FROM Test a WHERE a.publicId = :publicId",
                pl.edu.medicore.test.model.Test.class).setParameter("publicId", id).getSingleResult();

        assertNotNull(test);
        assertEquals(LocalDate.of(2026, 1, 10), test.getDate());
    }

    @Test
    void shouldReturn401_whenAccessedTestUploadWithInvalidToken() throws Exception {
        mockMvc.perform(post("/tests")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403_whenAccessedTestUploadAsAdmin() throws Exception {
        obtainRoleBasedToken(Role.ADMIN);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        mockMvc.perform(
                        multipart("/tests")
                                .file(file)
                                .param("date", "2026-01-10")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn400_whenValidationErrorsInTestCreate() throws Exception {
        obtainRoleBasedToken(Role.PATIENT);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        mockMvc.perform(
                        multipart("/tests")
                                .file(file)
                                .param("date", "2040-01-10")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Validation failed"))
                .andExpect(jsonPath("$.error.validationErrors").isArray())
                .andExpect(jsonPath("$.error.validationErrors.length()").value(1));
    }
}
