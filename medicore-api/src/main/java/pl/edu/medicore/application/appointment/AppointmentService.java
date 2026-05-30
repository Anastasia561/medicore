package pl.edu.medicore.application.appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.medicore.application.appointment.dto.AppointmentCreateDto;
import pl.edu.medicore.application.appointment.dto.AppointmentFilterDto;
import pl.edu.medicore.application.appointment.dto.AppointmentInfoDto;
import pl.edu.medicore.application.consultation.Workday;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;
import pl.edu.medicore.common.encryption.HashId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Page<AppointmentInfoDto> getAppointmentsInRange(AppointmentFilterDto filter, Pageable pageable);

    void cancel(HashId id);

    HashId create(HashId patientId, AppointmentCreateDto dto);

    Appointment getById(HashId id);

    List<LocalTime> getAvailableTimes(HashId doctorId, LocalDate date);

    long getTotalAppointmentsToday();

    long getTotalAppointmentsTodayByDoctorId(HashId id);

    List<ConsultationStatisticsDto> getMonthlyStatistics();

    List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(HashId id);

    long getDistinctPatientsByDoctorId(HashId doctorId);

    List<Appointment> getAllAppointmentByStatusAndDate(Status status, LocalDate date);

    void sendReminderAboutAppointmentsBetween(LocalDateTime from, LocalDateTime to);

    List<HashId> findIdsForCancellation(HashId doctorId, Workday dayOfWeek, LocalTime start, LocalTime end);
}
