package pl.edu.medicore.doctor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.doctor.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
