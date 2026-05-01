package pl.edu.medicore.consultation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.consultation.dto.ConsultationCreateDto;
import pl.edu.medicore.consultation.dto.ConsultationDto;
import pl.edu.medicore.consultation.dto.ConsultationUpdateDto;
import pl.edu.medicore.consultation.service.ConsultationService;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultations")
@Tag(name = "Consultations", description = "Endpoints for managing doctor schedule (consultations)")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @Operation(summary = "Get all doctor consultations")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseWrapper<List<ConsultationDto>> getAllForDoctor(@PathVariable UUID doctorId) {
        return ResponseWrapper.ok(consultationService.findByDoctorId(doctorId));
    }

    @Operation(summary = "Create consultation")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<UUID> create(@RequestBody @Valid ConsultationCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, consultationService.create(dto));
    }

    @Operation(summary = "Update doctor schedule")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{consultationId}")
    public ResponseWrapper<UUID> update(@RequestBody @Valid ConsultationUpdateDto dto, @PathVariable UUID consultationId) {
        return ResponseWrapper.ok(consultationService.update(consultationId, dto));
    }

    @Operation(summary = "Delete doctor consultation")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{consultationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID consultationId) {
        consultationService.delete(consultationId);
    }
}
