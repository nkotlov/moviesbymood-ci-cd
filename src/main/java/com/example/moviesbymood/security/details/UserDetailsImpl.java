package com.example.moviesbymood.security.details;

import com.example.moviesbymood.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final User user;
    public UserDetailsImpl(User user) { this.user = user; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override public String getPassword()  { return user.getUserPassword(); }
    @Override public String getUsername()  { return user.getUserEmail();    }
    @Override public boolean isAccountNonExpired()   { return true; }
    @Override public boolean isAccountNonLocked()    { return true; }
    @Override
    public boolean isCredentialsNonExpired() {
        return !user.isOauthOnly();
    }
    @Override public boolean isEnabled()             { return user.isConfirmed(); }
}
