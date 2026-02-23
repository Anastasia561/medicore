package pl.edu.medicore.auth.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.medicore.person.model.Role;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String passwordHash;
    private Role role;

    public String getRoleName() {
        return "ROLE_" + role.name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRoleName()));
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
