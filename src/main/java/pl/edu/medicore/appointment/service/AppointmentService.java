package pl.edu.medicore.appointment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;

import java.time.LocalDate;

public interface AppointmentService {
    Page<AppointmentInfoDto> getAppointmentsInRange(Long id, LocalDate start, LocalDate end, Pageable pageable);
}
