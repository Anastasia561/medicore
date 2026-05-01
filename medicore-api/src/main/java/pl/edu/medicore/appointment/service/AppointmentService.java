package pl.edu.medicore.appointment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    Page<AppointmentInfoDto> getAppointmentsInRange(AppointmentFilterDto filter, Pageable pageable);

    void cancel(UUID id);

    UUID create(long patientId, AppointmentCreateDto dto);

    Appointment getByPublicId(UUID id);

    List<LocalTime> getAvailableTimes(UUID doctorId, LocalDate date);

    long getTotalAppointmentsToday();

    long getTotalAppointmentsTodayByDoctorId(UUID id);

    List<ConsultationStatisticsDto> getMonthlyStatistics();

    List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(UUID id);

    long getDistinctPatientsByDoctorId(UUID doctorId);

    List<Appointment> getAllAppointmentByStatusAndDate(Status status, LocalDate date);

    void sendReminderAboutAppointmentsBetween(LocalDateTime from, LocalDateTime to);
}
