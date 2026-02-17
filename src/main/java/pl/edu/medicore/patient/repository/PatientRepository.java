package pl.edu.medicore.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.patient.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
