package pl.edu.medicore.application.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
