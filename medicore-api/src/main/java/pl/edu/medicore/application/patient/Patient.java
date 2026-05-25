package pl.edu.medicore.application.patient;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import pl.edu.medicore.application.Address;
import pl.edu.medicore.application.person.Person;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Patient extends Person {
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id")
    private Address address;
    @Column(nullable = false)
    private double weight;
    @Column(nullable = false)
    private boolean pregnant;
    @Column(nullable = false)
    private double height;
}
