package pl.edu.medicore.application.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
    Optional<Record> findByAppointmentPublicId(UUID id);

    Optional<Record> findByPublicId(UUID id);
}
