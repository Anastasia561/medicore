package pl.edu.medicore.application.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    Optional<Record> findByAppointmentId(long id);
}
