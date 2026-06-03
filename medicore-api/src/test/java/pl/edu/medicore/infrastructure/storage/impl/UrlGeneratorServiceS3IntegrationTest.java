package pl.edu.medicore.infrastructure.storage.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import pl.edu.medicore.AbstractIntegrationTest;
import pl.edu.medicore.common.exception.FileNotFoundException;

import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlGeneratorServiceS3IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private StorageServiceImpl storageService;

    @Autowired
    private UrlGeneratorServiceImpl urlGeneratorService;

    @Test
    void shouldGenerateViewUrl_whenFileExists() {
        UUID storageKey = UUID.randomUUID();
        String key = "test/%s/report".formatted(storageKey);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );
        storageService.uploadFile(file, storageKey);

        URL url = urlGeneratorService.generateViewUrl(storageKey);

        assertNotNull(url);
        String urlString = url.toString();
        assertTrue(urlString.contains(key));
        assertTrue(urlString.startsWith("http"));

        storageService.deleteFile(storageKey);
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExistForViewUrl() {
        UUID storageKey = UUID.randomUUID();

        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> urlGeneratorService.generateViewUrl(storageKey));

        assertEquals("File not found", exception.getMessage());
    }

    @Test
    void shouldGenerateDownloadUrl_whenFileExists() {
        UUID storageKey = UUID.randomUUID();
        String key = "test/%s/report".formatted(storageKey);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );
        storageService.uploadFile(file, storageKey);

        URL url = urlGeneratorService.generateDownloadUrl(storageKey);

        assertNotNull(url);
        String urlString = url.toString();
        assertTrue(urlString.contains(key));
        assertTrue(urlString.startsWith("http"));

        storageService.deleteFile(storageKey);
    }

    @Test
    void shouldThrowFileNotFoundException_whenFileDoesNotExistForDownloadUrl() {
        UUID storageKey = UUID.randomUUID();

        FileNotFoundException exception = assertThrows(FileNotFoundException.class,
                () -> urlGeneratorService.generateDownloadUrl(storageKey));

        assertEquals("File not found", exception.getMessage());
    }
}
