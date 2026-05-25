package pl.edu.medicore.application.test;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findTopByPatientIdOrderByDateDesc(Long patientId);
}
