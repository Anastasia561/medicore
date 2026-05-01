package pl.edu.medicore.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.appointment.model.Appointment;
import pl.edu.medicore.appointment.model.Status;
import pl.edu.medicore.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    @Query("""
            SELECT a.time
            FROM Appointment a
            WHERE a.doctor.publicId = :doctorId
            AND a.date = :date
            AND a.status = 'SCHEDULED'
            """)
    List<LocalTime> getScheduledTimesForDoctorAndDate(UUID doctorId, LocalDate date);

    long countByDate(LocalDate date);

    Optional<Appointment> findByPublicId(UUID uuid);

    long countByDateAndDoctorPublicId(LocalDate date, UUID doctorId);

    @Query("""
            SELECT new pl.edu.medicore.statistics.dto.ConsultationStatisticsDto(
                 MONTH(a.date),
                 a.status,
                 COUNT(a)
            )
            FROM Appointment a
            WHERE YEAR(a.date) = :year
            GROUP BY MONTH(a.date), a.status
            ORDER BY MONTH(a.date)
            """)
    List<ConsultationStatisticsDto> getMonthlyStatistics(int year);

    @Query("""
            SELECT new pl.edu.medicore.statistics.dto.ConsultationStatisticsDto(
                 MONTH(a.date),
                 a.status,
                 COUNT(a)
            )
            FROM Appointment a
            WHERE YEAR(a.date) = :year AND a.doctor.publicId= :id
            GROUP BY MONTH(a.date), a.status
            ORDER BY MONTH(a.date)
            """)
    List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(UUID id, int year);

    @Query("""
            SELECT COUNT(DISTINCT a.patient.id)
            FROM Appointment a
            WHERE a.doctor.publicId = :doctorId
            """)
    long countDistinctPatientsByDoctorId(UUID doctorId);

    @Query("""
            SELECT a FROM Appointment a WHERE a.status = :status AND a.date = :date
            """)
    List<Appointment> findAllByStatusAndDate(Status status, LocalDate date);

    @Query(value = """
                SELECT *
                FROM appointment a
                WHERE a.status = 'SCHEDULED'
                AND (a.date + a.time) BETWEEN :from AND :to
                AND a.reminder_sent = false
            """, nativeQuery = true)
    List<Appointment> getAppointmentsBetween(LocalDateTime from, LocalDateTime to);
}
