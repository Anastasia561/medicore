package pl.edu.medicore.risk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.risk.model.RiskResult;

import java.util.List;
import java.util.UUID;

public interface RiskResultRepository extends JpaRepository<RiskResult, Long> {
    @Query("""
                SELECT r
                FROM RiskResult r
                WHERE r.patient.publicId = :patientId
                ORDER BY r.calculatedAt DESC
                LIMIT 3
            """)
    List<RiskResult> getLatestByPatientPublicId(UUID patientId);
}
