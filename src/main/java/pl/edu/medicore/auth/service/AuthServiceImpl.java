package pl.edu.medicore.auth.service;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.edu.medicore.auth.core.CustomUserDetails;
import pl.edu.medicore.auth.dto.AuthRequestDto;
import pl.edu.medicore.auth.dto.TokenResponseDto;
import pl.edu.medicore.auth.jwt.service.JwtService;
import pl.edu.medicore.auth.refreshtoken.service.RefreshTokenService;
import pl.edu.medicore.exception.InvalidRefreshTokenException;
import pl.edu.medicore.person.model.Person;
import pl.edu.medicore.person.service.PersonService;

@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PersonService personService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenResponseDto login(AuthRequestDto request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(customUser);
            String refreshToken = jwtService.generateRefreshToken(customUser);

            Person person = personService.getByEmail(customUser.getUsername());

            refreshTokenService.create(person, refreshToken);

            return new TokenResponseDto(accessToken, refreshToken);
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public TokenResponseDto refresh(String refreshToken) {
        try {
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new InvalidRefreshTokenException("Token is not a refresh token");
            }

            String username = jwtService.extractUsername(refreshToken);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                throw new InvalidRefreshTokenException("Refresh token is invalid");
            }
            String newAccess = jwtService.generateAccessToken(userDetails);

            return new TokenResponseDto(newAccess, refreshToken);

        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
    }

    @Override
    public void logout(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        if (!jwtService.isRefreshToken(refreshToken) || !jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        refreshTokenService.revoke(refreshToken);
    }
}
