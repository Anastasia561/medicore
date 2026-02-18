package pl.edu.medicore.appointment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.service.AppointmentService;

import java.time.LocalDate;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    public Page<AppointmentInfoDto> getAppointmentsInDateRange(
            @RequestParam Long id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable
    ) {
        return appointmentService.getAppointmentsInRange(id, startDate, endDate, pageable);
    }
}
