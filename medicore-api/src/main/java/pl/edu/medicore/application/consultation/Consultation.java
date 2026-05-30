package pl.edu.medicore.application.consultation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.doctor.Doctor;

import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private Workday workday;
    @Column(name = "start_work_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_work_time", nullable = false)
    private LocalTime endTime;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}
