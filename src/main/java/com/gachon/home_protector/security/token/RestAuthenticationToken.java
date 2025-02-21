package com.gachon.home_protector.security.token;

import lombok.Builder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class RestAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private final Object credentials;

    @Builder
    private RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
    }

    public static RestAuthenticationToken createUnAuthenticatedToken(Object principal, Object credentials) {
        RestAuthenticationToken token = RestAuthenticationToken.builder()
                .authorities(null)
                .principal(principal)
                .credentials(credentials)
                .build();

        token.setAuthenticated(false);

        return token;
    }

    public static RestAuthenticationToken createAuthenticatedToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        RestAuthenticationToken token = RestAuthenticationToken.builder()
                .authorities(authorities)
                .principal(principal)
                .credentials(credentials)
                .build();

        token.setAuthenticated(true);

        return token;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
