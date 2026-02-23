package pl.edu.medicore.person.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.model.Role;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("""
            SELECT p.role FROM Person p
            WHERE  p.id=:id
            """)
    Optional<Role> getRole(Long id);

    Optional<Person> findByEmail(String email);
}
