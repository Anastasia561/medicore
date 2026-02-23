package pl.edu.medicore.auth.refreshtoken.service;

import pl.edu.medicore.person.model.Person;

public interface RefreshTokenService {

    void create(Person person, String tokenValue);

    void revoke(String tokenValue);
}
