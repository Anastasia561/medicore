package pl.edu.medicore.application.labresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface LabResultRepository extends JpaRepository<LabResult, Long> {
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
    List<LabResult> getLatestLabResultsByPatientId(long patientId);
}
