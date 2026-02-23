package pl.edu.medicore.appointment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Page<AppointmentInfoDto> getAppointmentsInRange(Long id, LocalDate start, LocalDate end, Pageable pageable);

    void updateStatus(Long id, Status status);

//    long create(Long patientId, Long doctorId, LocalDate date, LocalTime time);

    //    List<LocalTime> getAvailableTimes(Long doctorId, LocalDate date);

    Appointment getById(Long id);
}
