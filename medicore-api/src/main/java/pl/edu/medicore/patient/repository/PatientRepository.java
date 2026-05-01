package pl.edu.medicore.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pl.edu.medicore.patient.model.Patient;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPublicId(UUID publicId);
}
