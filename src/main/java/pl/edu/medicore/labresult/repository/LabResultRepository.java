package pl.edu.medicore.labresult.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.labresult.model.LabResult;

public interface LabResultRepository extends JpaRepository<LabResult, String> {
}
