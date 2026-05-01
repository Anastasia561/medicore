package pl.edu.medicore.doctor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.doctor.model.Doctor;
import pl.edu.medicore.statistics.dto.DoctorStatisticsDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    @Query("""
            SELECT new pl.edu.medicore.statistics.dto.DoctorStatisticsDto(
                d.specialization,
                COUNT(d)
            )
            FROM Doctor d
            GROUP BY d.specialization
            """)
    List<DoctorStatisticsDto> countDoctorsBySpecialization();

    boolean existsByPublicId(UUID publicId);

    Optional<Doctor> findByPublicId(UUID publicId);
}
