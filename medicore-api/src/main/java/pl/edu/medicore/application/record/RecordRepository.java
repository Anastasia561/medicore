package pl.edu.medicore.application.record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface RecordRepository extends JpaRepository<Record, Long>, JpaSpecificationExecutor<Record> {
}
