package com.gachon.home_protector.domain.security.provider;

import com.gachon.home_protector.domain.token.token.RestAuthenticationToken;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // passwordEncoder 로 encoding 된 pw 가 db 에 없을 경우 determineCorrectPassword() 주석화하기
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        RestUserDetails userDetails = (RestUserDetails) restUserDetailsService.loadUserByUsername(userId);

        String rawPassword = (String) authentication.getCredentials();
        String encodedPassword = userDetails.getPassword();

//        if (!rawPassword.equals(encodedPassword)) {
//            throw new BadCredentialsException("");
//        }
        determineCorrectPassword(authentication, userDetails);

        return RestAuthenticationToken.createAuthenticatedToken(userDetails.getAuthorities(), userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RestAuthenticationToken.class);
    }

    private void determineCorrectPassword(Authentication authentication, RestUserDetails userDetails) {
        String rawPassword = (String) authentication.getCredentials();
        String encodedPassword = userDetails.getPassword();
        if (isInvalidPassword(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("");
        }
    }

    private boolean isInvalidPassword(String rawPassword, String encodedPassword) {
        return !passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
