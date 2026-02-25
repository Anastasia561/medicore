package pl.edu.medicore.appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.appointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
            SELECT a FROM Appointment a
            WHERE  a.doctor.id = :doctorId
            AND a.date BETWEEN :startDate AND :endDate
            """)
    Page<Appointment> findByDoctorIdAndDateBetween(Long doctorId, LocalDate startDate,
                                                   LocalDate endDate, Pageable pageable
    );

    @Query("""
            SELECT a FROM Appointment a
            WHERE  a.patient.id = :patientId
            AND a.date BETWEEN :startDate AND :endDate
            """)
    Page<Appointment> findByPatientIdAndDateBetween(Long patientId, LocalDate startDate,
                                                    LocalDate endDate, Pageable pageable
    );

    @Query("""
            SELECT a.time
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.date = :date
            AND a.status = 'SCHEDULED'
            """)
    List<LocalTime> getScheduledTimesForDoctorAndDate(Long doctorId, LocalDate date);

}
