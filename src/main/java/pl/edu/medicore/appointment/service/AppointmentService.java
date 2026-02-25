package pl.edu.medicore.appointment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Page<AppointmentInfoDto> getAppointmentsInRange(Long id, LocalDate start, LocalDate end, Pageable pageable);

    void cancel(Long id);

    long create(Long patientId, AppointmentCreateDto dto);

    Appointment getById(Long id);

    List<LocalTime> getAvailableTimes(Long doctorId, LocalDate date);
}
