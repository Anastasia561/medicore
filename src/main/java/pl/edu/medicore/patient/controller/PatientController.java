package pl.edu.medicore.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.patient.dto.PatientRegisterDto;
import pl.edu.medicore.patient.dto.PatientResponseDto;
import pl.edu.medicore.patient.service.PatientService;
import pl.edu.medicore.person.model.Status;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.service.VerificationTokenService;
import pl.edu.medicore.wrapper.ResponseWrapper;

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

    @Operation(summary = "Register patient")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseWrapper<Long> register(@RequestBody @Valid PatientRegisterDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, patientService.register(dto));
    }

    @Operation(summary = "Verify email after registration for profile activation")
    @PostMapping("/verify-email")
    public void verifyEmail(@RequestParam String token, @RequestParam String email) {
        tokenService.validateToken(token, TokenType.EMAIL_VERIFICATION, email);
        patientService.updateStatus(email, Status.ACTIVE);
    }
}
