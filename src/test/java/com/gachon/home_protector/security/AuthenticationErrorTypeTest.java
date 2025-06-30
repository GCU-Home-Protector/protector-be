package com.gachon.home_protector.security;

import com.gachon.home_protector.domain.security.AuthenticationErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationErrorTypeTest {

    @DisplayName("인증 예외 종류에 따른 메시지를 가져올 수 있다.")
    @MethodSource("authenticationExceptionParameters")
    @ParameterizedTest(name = "{1}")
    void findErrorMessageByException(AuthenticationException authException, String errorMessage) {
        // given

        // when
        String result = AuthenticationErrorType.findErrorMessageByException(authException);

        // then
        assertThat(result).isEqualTo(errorMessage);
    }

    static Stream<Arguments> authenticationExceptionParameters() {
        return Stream.of(
                Arguments.of(new BadCredentialsException(""), "ID 혹은 비밀번호가 잘못되었습니다."),
                Arguments.of(new UsernameNotFoundException(""), "유저가 존재하지 않습니다."),
                Arguments.of(new CredentialsExpiredException(""), "만료되었습니다!"),
                Arguments.of(new DisabledException(""), "Unknown authentication error"));
    }
}
