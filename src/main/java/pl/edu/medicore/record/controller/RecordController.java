package pl.edu.medicore.record.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.record.dto.RecordCreateDto;
import pl.edu.medicore.record.dto.RecordDto;
import pl.edu.medicore.record.dto.RecordFilterDto;
import pl.edu.medicore.record.dto.RecordPreviewDto;
import pl.edu.medicore.record.service.RecordService;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/records")
@Tag(name = "Records", description = "Endpoints for managing medical records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    @Operation(summary = "Find medical record by appointment id")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseWrapper<RecordDto> getByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseWrapper.ok(recordService.getByAppointmentId(appointmentId));
    }

    @Operation(summary = "Get page of medical records with filtering")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping
    public ResponseWrapper<Page<RecordPreviewDto>> getAllPageable(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid RecordFilterDto filter,
            Pageable pageable) {
        return ResponseWrapper.ok(recordService.getAllById(userDetails, filter, pageable));
    }

    @Operation(summary = "Create medical record after consultation")
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> create(@Valid @RequestBody RecordCreateDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, recordService.create(dto));
    }
}
