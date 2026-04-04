package pl.edu.medicore.infrastructure.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import pl.edu.medicore.AbstractS3IntegrationTest;
import pl.edu.medicore.exception.FileNotFoundException;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlGeneratorServiceS3IntegrationTest extends AbstractS3IntegrationTest {

    @Autowired
    private StorageServiceImpl storageService;

    @Autowired
    private UrlGeneratorServiceImpl urlGeneratorService;

    @Test
    void shouldGenerateViewUrl_whenFileExists() {
        Long testId = 300L;
        String key = "test/%d/report".formatted(testId);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );
        storageService.uploadFile(file, testId);

        URL url = urlGeneratorService.generateViewUrl(testId);

        assertNotNull(url);
        String urlString = url.toString();
        assertTrue(urlString.contains(key));
        assertTrue(urlString.startsWith("http"));

        storageService.deleteFile(testId);
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExistForViewUrl() {
        Long testId = 999L;

        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> urlGeneratorService.generateViewUrl(testId));

        assertEquals("File not found", exception.getMessage());
    }

    @Test
    void shouldGenerateDownloadUrl_whenFileExists() {
        Long testId = 300L;
        String key = "test/%d/report".formatted(testId);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );
        storageService.uploadFile(file, testId);

        URL url = urlGeneratorService.generateDownloadUrl(testId);

        assertNotNull(url);
        String urlString = url.toString();
        assertTrue(urlString.contains(key));
        assertTrue(urlString.startsWith("http"));

        storageService.deleteFile(testId);
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExistForDownloadUrl() {
        Long testId = 999L;

        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> urlGeneratorService.generateDownloadUrl(testId));

        assertEquals("File not found", exception.getMessage());
    }
}
