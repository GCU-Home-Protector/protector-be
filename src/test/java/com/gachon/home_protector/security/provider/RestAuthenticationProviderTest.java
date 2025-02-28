package com.gachon.home_protector.security.provider;

import com.gachon.home_protector.MockTestSupport;
import com.gachon.home_protector.security.token.RestAuthenticationToken;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.user.dto.RestUserLoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

/**
 * PasswordEncoder 때문에 Mockito 통해 테스트
 */
class RestAuthenticationProviderTest extends MockTestSupport {

    @DisplayName("인증 객체에 대해 인증을 수행할 수 있다.")
    @Test
    void authenticate() {
        // given
        Long id = 1L;
        String userId = "userId";
        String password = "password";
        String role = "ROLE";

        RestAuthenticationToken restAuthenticationToken =
                RestAuthenticationToken.createUnAuthenticatedToken(userId, password);

        RestUserLoginResponse restUserLoginResponse = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails userDetails = new RestUserDetails(restUserLoginResponse);

        given(restUserDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        RestAuthenticationToken result = (RestAuthenticationToken) restAuthenticationProvider.authenticate(restAuthenticationToken);

        // then
        assertThat(result.getPrincipal())
                .extracting("id", "username", "password")
                .containsExactlyInAnyOrder(id, userId, password);

        assertThat(result)
                .extracting("credentials", "authorities")
                .containsExactlyInAnyOrder(null, List.of(new SimpleGrantedAuthority(role)));
    }

    @DisplayName("비밀번호가 다르면 인증할 수 없다.")
    @Test
    void authenticate_INVALID_PASSWORD() {
        // given
        Long id = 1L;
        String userId = "userId";
        String password = "password";
        String role = "ROLE";

        RestAuthenticationToken restAuthenticationToken =
                RestAuthenticationToken.createUnAuthenticatedToken(userId, password);

        RestUserLoginResponse restUserLoginResponse = new RestUserLoginResponse(id, userId, password, role);
        RestUserDetails userDetails = new RestUserDetails(restUserLoginResponse);

        given(restUserDetailsService.loadUserByUsername(anyString())).willReturn(userDetails);

        // passwordEncoder에서 false 반환
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when // then
        assertThatThrownBy(() -> restAuthenticationProvider.authenticate(restAuthenticationToken))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("");
    }

    @DisplayName("user를 찾지 못할 수 있다.")
    @Test
    void authenticate_NOT_FOUND_USER() {
        // given
        String userId = "userId";
        String password = "password";

        RestAuthenticationToken restAuthenticationToken =
                RestAuthenticationToken.createUnAuthenticatedToken(userId, password);

        // User 존재하지 않는 경우 UsernameNotFoundException 반환
        given(restUserDetailsService.loadUserByUsername(userId)).willThrow(new UsernameNotFoundException("존재하지 않는 유저입니다!"));

        // when // then
        assertThatThrownBy(() -> restAuthenticationProvider.authenticate(restAuthenticationToken))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("존재하지 않는 유저입니다!");
    }
}