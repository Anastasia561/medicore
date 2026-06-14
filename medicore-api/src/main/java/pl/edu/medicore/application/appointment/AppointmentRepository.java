package pl.edu.medicore.application.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.date = :date
            AND a.status = 'SCHEDULED'
            """)
    List<Appointment> getScheduledAppointments(long doctorId, LocalDate date);

    long countByDate(LocalDate date);

    long countByDateAndDoctorId(LocalDate date, long doctorId);

    @Query("""
            SELECT new pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto(
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
            SELECT new pl.edu.medicore.application.statistics.dto.ConsultationStatisticsDto(
                 MONTH(a.date),
                 a.status,
                 COUNT(a)
            )
            FROM Appointment a
            WHERE YEAR(a.date) = :year AND a.doctor.id= :id
            GROUP BY MONTH(a.date), a.status
            ORDER BY MONTH(a.date)
            """)
    List<ConsultationStatisticsDto> getMonthlyStatisticsByDoctorId(long id, int year);

    @Query("""
            SELECT COUNT(DISTINCT a.patient.id)
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            """)
    long countDistinctPatientsByDoctorId(long doctorId);

    @Query("""
            SELECT a FROM Appointment a WHERE a.status = :status AND a.date = :date
            """)
    List<Appointment> findAllByStatusAndDate(AppointmentStatus status, LocalDate date);

    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.status = 'SCHEDULED'
            AND a.reminderSent = false
            AND CAST(CONCAT(a.date, ' ', a.startTime) AS localdatetime) BETWEEN :from AND :to
            """)
    List<Appointment> getAppointmentsBetween(LocalDateTime from, LocalDateTime to);

    @Query(value = """
            SELECT a.id
            FROM appointment a
            WHERE a.doctor_id = :doctorId
            AND a.status = 'SCHEDULED'
            AND a.date >= CURRENT_DATE
            AND TRIM(TO_CHAR(a.date, 'DAY')) = :dayOfWeek
            AND a.start_time < :end
            AND a.end_time > :start
            """, nativeQuery = true)
    List<Long> findIdsForCancellation(long doctorId, String dayOfWeek, LocalTime start, LocalTime end);
}
