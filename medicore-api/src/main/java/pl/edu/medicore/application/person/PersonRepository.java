package pl.edu.medicore.application.person;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("""
            SELECT p.role FROM Person p
            WHERE  p.publicId=:id
            """)
    Optional<Role> getRoleByPublicId(UUID id);

    @Query("""
            SELECT p.role FROM Person p
            WHERE  p.id=:id
            """)
    Optional<Role> getRoleById(long id);

    Optional<Person> findByPublicId(UUID id);

    Optional<Person> findByEmail(String email);

    boolean existsByEmail(String email);
}
