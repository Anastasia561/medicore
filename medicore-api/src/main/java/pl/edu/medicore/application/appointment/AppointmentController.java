package pl.edu.medicore.application.appointment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.auth.CustomUserDetails;
import pl.edu.medicore.common.encryption.HashId;
import pl.edu.medicore.common.wrapper.ResponseWrapper;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@Tag(name = "Appointments", description = "Endpoints for managing appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Get page of appointments in date range with filtering")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseWrapper<List<? extends AppointmentInfoDto>> getAppointmentsInDateRangeForAdmin(
            @Valid AppointmentFilterDto filter,
            @PathVariable HashId userId) {
        return ResponseWrapper.ok(appointmentService.getAppointmentsInRange(userId, filter));
    }

    @Operation(summary = "Get page of appointments in date range with filtering")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    @GetMapping
    public ResponseWrapper<List<? extends AppointmentInfoDto>> getAppointmentsInDateRange(
            @Valid AppointmentFilterDto filter,
            @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseWrapper.ok(appointmentService.getAppointmentsInRange(user.getId(), filter));
    }

    @Operation(summary = "Cancel appointment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @PutMapping("/cancel/{id}")
    public void cancel(@PathVariable HashId id) {
        appointmentService.cancel(id);
    }

    @Operation(summary = "Schedule an appointment")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<HashId> create(
            @Valid @RequestBody AppointmentCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        HashId patientId = userDetails.getId();
        HashId appointmentId = appointmentService.create(patientId, dto);
        return ResponseWrapper.withStatus(HttpStatus.CREATED, appointmentId);
    }
}
