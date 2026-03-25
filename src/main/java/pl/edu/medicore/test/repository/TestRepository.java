package pl.edu.medicore.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.test.model.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
