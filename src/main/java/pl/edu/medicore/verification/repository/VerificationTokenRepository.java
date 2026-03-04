package pl.edu.medicore.verification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.medicore.verification.model.TokenType;
import pl.edu.medicore.verification.model.VerificationToken;

import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query("""
                SELECT t FROM VerificationToken t
                WHERE t.email = :email
                AND t.tokenType = :type
                AND t.expiresAt > CURRENT_TIMESTAMP
            """)
    List<VerificationToken> findActiveTokensByEmailAndType(
            @Param("email") String email,
            @Param("type") TokenType type
    );
}
