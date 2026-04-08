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
}
