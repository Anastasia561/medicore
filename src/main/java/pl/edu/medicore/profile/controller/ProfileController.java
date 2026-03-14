package pl.edu.medicore.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.profile.dto.PatientProfileUpdateDto;
import pl.edu.medicore.profile.dto.ProfileResponseDto;
import pl.edu.medicore.profile.dto.ProfileUpdateDto;
import pl.edu.medicore.profile.service.ProfileService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "Endpoints for managing user profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "Get user profile info")
    @GetMapping
    public ResponseWrapper<ProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        long id = userDetails.getId();
        return ResponseWrapper.ok(profileService.getProfileById(id));
    }

    @Operation(summary = "Update profile info for admin or doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @PutMapping
    public ResponseWrapper<Long> updateProfile(@Valid @RequestBody ProfileUpdateDto dto,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseWrapper.ok(profileService.updateProfile(dto, userDetails.getId()));
    }

    @Operation(summary = "Update patient profile info")
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/patient")
    public ResponseWrapper<Long> updatePatientProfile(@Valid @RequestBody PatientProfileUpdateDto dto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseWrapper.ok(profileService.updatePatientProfile(dto, userDetails.getId()));
    }
}
