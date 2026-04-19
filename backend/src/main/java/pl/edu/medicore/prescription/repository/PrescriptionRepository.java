package pl.edu.medicore.prescription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.prescription.model.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long>{
}
