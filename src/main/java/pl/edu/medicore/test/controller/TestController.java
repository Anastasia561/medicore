package pl.edu.medicore.test.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.test.dto.TestUploadRequestDto;
import pl.edu.medicore.test.service.contract.TestService;
import pl.edu.medicore.test.service.contract.UrlGeneratorService;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.net.URL;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    private final UrlGeneratorService urlGeneratorService;

    @GetMapping("/view/{id}")
    public ResponseWrapper<URL> getViewUrl(@PathVariable long id) {
        return ResponseWrapper.ok(urlGeneratorService.generateViewUrl(id));
    }

    @GetMapping("/download/{id}")
    public ResponseWrapper<URL> getDownloadUrl(@PathVariable long id) {
        return ResponseWrapper.ok(urlGeneratorService.generateDownloadUrl(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseWrapper<Long> upload(@ModelAttribute @Valid TestUploadRequestDto dto,
                                        @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, testService.save(dto, user.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        testService.delete(id);
    }
}
