package pl.edu.medicore.appointment.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.wrapper.ResponseWrapper;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Get page of appointments in date range with filtering")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @GetMapping
    public ResponseWrapper<Page<AppointmentInfoDto>> getAppointmentsInDateRange(
            @Valid AppointmentFilterDto filter,
            Pageable pageable) {
        return ResponseWrapper.ok(appointmentService.getAppointmentsInRange(filter, pageable));
    }

    @Operation(summary = "Cancel appointment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @PutMapping("/cancel/{id}")
    public void cancel(@PathVariable Long id) {
        appointmentService.cancel(id);
    }

    @Operation(summary = "Schedule an appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> create(
            @Valid @RequestBody AppointmentCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long patientId = userDetails.getId();
        Long appointmentId = appointmentService.create(patientId, dto);
        return ResponseWrapper.withStatus(HttpStatus.CREATED, appointmentId);
    }
}
