package pl.edu.medicore.auth.refreshtoken.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.medicore.auth.refreshtoken.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
