package pl.edu.medicore.application.auth.refreshtoken;

import pl.edu.medicore.application.person.Person;

import java.time.Instant;

public interface RefreshTokenService {

    void create(Person person, String tokenValue);

    void revoke(String tokenValue);

    void deleteAllExpiredBefore(Instant now);
}
