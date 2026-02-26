package pl.edu.medicore.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.medicore.record.model.Record;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    Optional<Record> findByAppointmentId(Long id);

    @Query("""
                SELECT r
                FROM Record r
                WHERE r.appointment.patient.id = :patientId
            """)
    Page<Record> findByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("""
                SELECT r
                FROM Record r
                WHERE r.appointment.doctor.id = :doctorId
            """)
    Page<Record> findByDoctorId(@Param("doctorId") Long doctorId, Pageable pageable);
}
