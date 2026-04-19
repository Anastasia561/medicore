package pl.edu.medicore.auth.refreshtoken.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.medicore.auth.jwt.JwtProperties;
import pl.edu.medicore.auth.refreshtoken.model.RefreshToken;
import pl.edu.medicore.auth.refreshtoken.repository.RefreshTokenRepository;
import pl.edu.medicore.person.model.Person;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public void create(Person person, String tokenValue) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setPerson(person);
        refreshToken.setExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationTimeMs()));
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revoke(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
        refreshTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void deleteAllExpiredBefore(Instant now) {
        refreshTokenRepository.deleteAllByExpiresAtBefore(now);
    }
}
