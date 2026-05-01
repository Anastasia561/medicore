package pl.edu.medicore.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.edu.medicore.record.model.Record;

import java.util.Optional;
import java.util.UUID;

public interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    Optional<Record> findByAppointmentPublicId(UUID id);
    Optional<Record> findByPublicId(UUID id);
}
