package pl.edu.medicore.appointment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.time.LocalDate;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseWrapper<Page<AppointmentInfoDto>> getAppointmentsInDateRange(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable
    ) {
        return ResponseWrapper.ok(appointmentService.getAppointmentsInRange(userId, startDate, endDate, pageable));
    }

    @PutMapping
    public void cancel(@RequestParam Long id) {
        appointmentService.cancel(id);
    }

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
