package pl.edu.medicore.doctor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorInvitationRequestDto;
import pl.edu.medicore.doctor.dto.DoctorRegistrationDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@Tag(name = "Doctors", description = "Endpoints for managing doctors data")
@RequiredArgsConstructor
public class DoctorController {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    @Operation(summary = "Get free slots for selected doctor")
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/{doctorId}/times")
    public ResponseWrapper<List<LocalTime>> getAvailableTimes(@PathVariable Long doctorId,
                                                              @RequestParam LocalDate date) {
        return ResponseWrapper.ok(appointmentService.getAvailableTimes(doctorId, date));
    }

    @Operation(summary = "Get page of doctors with filtering")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @GetMapping
    public ResponseWrapper<Page<DoctorResponseDto>> getAll(DoctorFilterDto filter, Pageable pageable) {
        return ResponseWrapper.ok(doctorService.getAll(filter, pageable));
    }

    @Operation(summary = "Invite doctor for registration")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inviteDoctor(@Valid @RequestBody DoctorInvitationRequestDto request) {
        doctorService.invite(request);
    }

    @Operation(summary = "Register a new doctor")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> register(@Valid @RequestBody DoctorRegistrationDto dto) {
        return ResponseWrapper.withStatus(HttpStatus.CREATED, doctorService.register(dto));
    }
}
