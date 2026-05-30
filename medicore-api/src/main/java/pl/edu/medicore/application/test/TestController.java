package pl.edu.medicore.application.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.application.auth.CustomUserDetails;
import pl.edu.medicore.application.test.dto.TestUploadRequestDto;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

import java.net.URL;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
@Tag(name = "Tests", description = "Endpoints for managing patients blood tests")
public class TestController {
    private final TestService testService;

    @Operation(summary = "Get presigned url to view blood test file")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/view/{id}")
    public ResponseWrapper<URL> getViewUrl(@PathVariable HashId id) {
        return ResponseWrapper.ok(testService.generateViewUrl(id));
    }

    @Operation(summary = "Get presigned url to download blood test file")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/download/{id}")
    public ResponseWrapper<URL> getDownloadUrl(@PathVariable HashId id) {
        return ResponseWrapper.ok(testService.generateDownloadUrl(id));
    }

    @Operation(summary = "Endpoint for uploading blood test file")
    @PreAuthorize("hasRole('PATIENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseWrapper<HashId> upload(@ModelAttribute @Valid TestUploadRequestDto dto,
                                          @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, testService.save(dto, user.getId()));
    }
}
