package pl.edu.medicore.labresult.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.labresult.model.LabResult;

import java.util.List;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    @Query("""
                SELECT lr
                FROM LabResult lr
                WHERE lr.test.id = :testId
            """)
    List<LabResult> getLabResultsByTestId(Long testId);

    @Query("""
                SELECT lr
                FROM LabResult lr
                WHERE lr.test.id = (
                    SELECT t.id FROM Test t
                    WHERE t.patient.id = :patientId
                    ORDER BY t.date DESC
                    LIMIT 1
                )
            """)
    List<LabResult> getLatestLabResultsByPatientId(Long patientId);
}
