package pl.edu.medicore.application.doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.application.statistics.dto.DoctorStatisticsDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    @Query("""
            SELECT new pl.edu.medicore.application.statistics.dto.DoctorStatisticsDto(
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
