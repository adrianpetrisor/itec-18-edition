package com.seventailed.engine.security;

import com.seventailed.engine.entity.EngineUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EngineUserDetails implements UserDetails, OAuth2User {
    private EngineUserEntity user;
    private boolean oauth2Authorized;
    private boolean googleAuthenticationAuthorized = false;

    public EngineUserDetails(EngineUserEntity user, boolean oauth2Authorized) {
        this.user = user;
        this.oauth2Authorized = oauth2Authorized;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public EngineUserEntity getUser() {
        return user;
    }

    public boolean isOauth2Authorized() {
        return oauth2Authorized;
    }

    public boolean isGoogleAuthenticationAuthorized() {
        return googleAuthenticationAuthorized;
    }

    public void setGoogleAuthenticationAuthorized(boolean googleAuthenticationAuthorized) {
        this.googleAuthenticationAuthorized = googleAuthenticationAuthorized;
    }
}
