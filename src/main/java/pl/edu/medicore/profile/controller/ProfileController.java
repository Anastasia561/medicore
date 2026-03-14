package pl.edu.medicore.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.service.ProfileService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseWrapper<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        long id = userDetails.getId();
        return ResponseWrapper.ok(profileService.getProfileById(id));
    }
}
