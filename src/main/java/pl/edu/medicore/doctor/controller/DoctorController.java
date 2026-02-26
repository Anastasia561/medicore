package pl.edu.medicore.doctor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.appointment.service.AppointmentService;
import pl.edu.medicore.doctor.dto.DoctorFilterDto;
import pl.edu.medicore.doctor.dto.DoctorResponseDto;
import pl.edu.medicore.doctor.service.DoctorService;
import pl.edu.medicore.wrapper.ResponseWrapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/{doctorId}/times")
    public ResponseWrapper<List<LocalTime>> getAvailableTimes(@PathVariable Long doctorId,
                                                              @RequestParam LocalDate date) {
        return ResponseWrapper.ok(appointmentService.getAvailableTimes(doctorId, date));
    }

    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @GetMapping
    public ResponseWrapper<Page<DoctorResponseDto>> getAll(DoctorFilterDto filter, Pageable pageable) {
        return ResponseWrapper.ok(doctorService.getAll(filter, pageable));
    }
}
