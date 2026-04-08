package pl.edu.medicore.risk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.medicore.patient.model.Patient;
import pl.edu.medicore.test.model.Test;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "risk_result")
public class RiskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "disease", nullable = false)
    private Disease disease;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_group", nullable = false)
    private RiskGroup riskGroup;

    @Column(name = "risk_percent")
    private Double riskPercent;

    @OneToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @CreationTimestamp
    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;
}
