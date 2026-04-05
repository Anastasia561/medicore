package pl.edu.medicore.auth.refreshtoken.service;

import pl.edu.medicore.person.model.Person;

import java.time.Instant;

public interface RefreshTokenService {

    void create(Person person, String tokenValue);

    void revoke(String tokenValue);

    void deleteAllExpiredBefore(Instant now);
}
