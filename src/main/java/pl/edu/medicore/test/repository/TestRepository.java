package pl.edu.medicore.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.test.model.Test;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findTopByPatientIdOrderByDateDesc(Long patientId);
}
