package pl.edu.medicore.risk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.risk.model.RiskResult;

public interface RiskResultRepository extends JpaRepository<RiskResult, Long> {
}
