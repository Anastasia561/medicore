package pl.edu.medicore.application.patient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.application.auth.CustomUserDetails;
import pl.edu.medicore.application.patient.dto.PatientMedicalProfileDto;
import pl.edu.medicore.application.patient.dto.PatientMedicalProfileUpdateDto;
import pl.edu.medicore.application.patient.dto.PatientRegisterDto;
import pl.edu.medicore.application.patient.dto.PatientResponseDto;
import pl.edu.medicore.application.patient.dto.PatientVerificationRequestDto;
import pl.edu.medicore.application.person.UserStatus;
import pl.edu.medicore.application.verification.TokenType;
import pl.edu.medicore.application.verification.VerificationTokenService;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patients", description = "Endpoints for managing patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;
    private final VerificationTokenService tokenService;

    @Operation(summary = "Get page of patients with searching possibility")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @GetMapping
    public ResponseWrapper<Page<PatientResponseDto>> getAllPageable(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseWrapper.ok(patientService.findAll(search, pageable));
    }

    @Operation(summary = "Get medical profile for the authenticated patient")
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/medical-profile")
    public ResponseWrapper<PatientMedicalProfileDto> getOwnMedicalProfile(
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseWrapper.ok(patientService.getMedicalProfile(user.getId()));
    }

    @Operation(summary = "Get medical profile for a patient")
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/{patientId}/medical-profile")
    public ResponseWrapper<PatientMedicalProfileDto> getMedicalProfileByPatientId(
            @PathVariable HashId patientId) {
        return ResponseWrapper.ok(patientService.getMedicalProfile(patientId));
    }

    @Operation(summary = "Update medical profile for the authenticated patient")
    @PreAuthorize("hasRole('PATIENT')")
    @PutMapping("/medical-profile")
    public ResponseWrapper<HashId> updateOwnMedicalProfile(
            @Valid @RequestBody PatientMedicalProfileUpdateDto dto,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseWrapper.ok(patientService.updateMedicalProfile(user.getId(), dto));
    }

    @Operation(summary = "Register patient")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseWrapper<Long> register(@RequestBody @Valid PatientRegisterDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, patientService.register(dto));
    }

    @Operation(summary = "Verify email after registration for profile activation")
    @PostMapping("/verify-email")
    public void verifyEmail(@Valid @RequestBody PatientVerificationRequestDto dto) {
        String email = tokenService.validateTokenAndGetEmail(dto.token(), TokenType.EMAIL_VERIFICATION);
        patientService.updateStatus(email, UserStatus.ACTIVE);
    }
}
