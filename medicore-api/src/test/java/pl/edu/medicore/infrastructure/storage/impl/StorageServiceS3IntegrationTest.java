package pl.edu.medicore.infrastructure.storage.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.common.exception.FileNotFoundException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StorageServiceS3IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StorageServiceImpl storageService;

    @Test
    void shouldUploadFile_whenInputIsValid() {
        UUID storageKey = UUID.randomUUID();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "blood-test.pdf",
                "application/pdf",
                "sample pdf content".getBytes()
        );

        storageService.uploadFile(file, storageKey);

        String expectedKey = "test/%s/report".formatted(storageKey);

        ResponseBytes<GetObjectResponse> downloadedFile = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(expectedKey)
                        .build()
        );

        assertThat(downloadedFile.asUtf8String()).isEqualTo("sample pdf content");
        storageService.deleteFile(storageKey);
    }

    @Test
    void shouldDeleteFile_whenFileExists() {
        UUID storageKey = UUID.randomUUID();

        String key = "test/%s/report".formatted(storageKey);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );
        storageService.uploadFile(file, storageKey);

        assertDoesNotThrow(() -> storageService.deleteFile(storageKey));
        assertThrows(Exception.class, () -> {
            s3Client.headObject(builder -> builder.bucket(bucketName).key(key).build());
        });
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExistForDelete() {
        UUID storageKey = UUID.randomUUID();

        FileNotFoundException ex = assertThrows(FileNotFoundException.class,
                () -> storageService.deleteFile(storageKey));
        assertEquals("File not found", ex.getMessage());
    }

    @Test
    void shouldReturnInputStream_whenFileExists() throws IOException {
        UUID storageKey = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );

        storageService.uploadFile(file, storageKey);

        InputStream inputStream = storageService.getFile(storageKey);
        assertNotNull(inputStream);

        String downloadedContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        assertEquals("Dummy content", downloadedContent);

        inputStream.close();
        storageService.deleteFile(storageKey);
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExist() {
        UUID storageKey = UUID.randomUUID();

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> storageService.getFile(storageKey));

        assertEquals("File not found", exception.getMessage());
    }
}
