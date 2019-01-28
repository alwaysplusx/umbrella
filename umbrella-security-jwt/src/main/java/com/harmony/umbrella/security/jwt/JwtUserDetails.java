package com.harmony.umbrella.security.jwt;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author wuxii
 */
public class JwtUserDetails implements UserDetails, CredentialsContainer {

    private final Long id;
    private final UserDetails userDetails;

    public JwtUserDetails(Long id, UserDetails userDetails) {
        this.id = id;
        this.userDetails = userDetails;
    }

    @Override
    public void eraseCredentials() {
        if (userDetails instanceof CredentialsContainer) {
            ((CredentialsContainer) userDetails).eraseCredentials();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userDetails.isEnabled();
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public String toString() {
        return "JwtUser[id=" + id + ", userDetails=" + userDetails + "]";
    }

}
