package pl.edu.medicore.doctor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.person.model.Person;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Doctor extends Person {
    @Column(name = "experience", nullable = false)
    private Integer experience;
    @Column(name = "employment_date", nullable = false)
    private LocalDate employmentDate;
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
}
