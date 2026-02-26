package pl.edu.medicore.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.appointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    @Query("""
            SELECT a.time
            FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.date = :date
            AND a.status = 'SCHEDULED'
            """)
    List<LocalTime> getScheduledTimesForDoctorAndDate(Long doctorId, LocalDate date);
}
