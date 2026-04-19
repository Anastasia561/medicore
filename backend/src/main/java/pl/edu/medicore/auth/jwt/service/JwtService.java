package pl.edu.medicore.auth.jwt.service;

import io.jsonwebtoken.Claims;
import pl.edu.medicore.auth.core.CustomUserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    String generateAccessToken(CustomUserDetails user);

    String generateRefreshToken(CustomUserDetails user);

    <T> T extractClaim(String token, Function<Claims, T> resolver);

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean isTokenExpired(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    boolean isTokenValid(String token, CustomUserDetails userDetails);
}
