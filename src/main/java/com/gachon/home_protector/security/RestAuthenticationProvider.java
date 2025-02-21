package com.gachon.home_protector.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("restAuthenticationProvider")
@RequiredArgsConstructor
public class RestAuthenticationProvider implements AuthenticationProvider {

    private final RestUserDetailsService restUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        RestUserDetails userDetails = (RestUserDetails) restUserDetailsService.loadUserByUsername(username);

        String rawPassword = (String) authentication.getCredentials();
        String encodedPassword = userDetails.getPassword();
//        if (isInvalidPassword(rawPassword, encodedPassword)) {
//            throw new BadCredentialsException("");
//        }

        return RestAuthenticationToken.createAuthenticatedToken(userDetails.getAuthorities(), userDetails, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RestAuthenticationToken.class);
    }

    private boolean isInvalidPassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
