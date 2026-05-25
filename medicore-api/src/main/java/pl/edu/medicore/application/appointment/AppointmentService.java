package pl.edu.medicore.application.appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;

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

    List<UUID> findIdsForCancellation(long doctorId, Workday dayOfWeek, LocalTime start, LocalTime end);
}
