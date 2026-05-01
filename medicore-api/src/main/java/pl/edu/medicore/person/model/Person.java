package pl.edu.medicore.person.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, updatable = false)
    private UUID publicId;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "first_name", length = 20, nullable = false)
    private String firstName;
    @Column(name = "last_name", length = 20, nullable = false)
    private String lastName;
    @Column(name = "email", length = 60, nullable = false, unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @PrePersist
    public void prePersist() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }
}
