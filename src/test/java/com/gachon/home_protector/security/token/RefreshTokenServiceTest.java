package com.gachon.home_protector.security.token;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.security.token.exception.ExpiredRefreshTokenException;
import com.gachon.home_protector.security.token.exception.InvalidAccessTokenException;
import com.gachon.home_protector.user.User;
import com.gachon.home_protector.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenServiceTest extends IntegrationTestSupport {

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAll();
    }

    @DisplayName("access 토큰 만료 시, at와 rt를 재발급받을 수 있다.")
    @Test
    void reIssueAccessAndRefreshToken() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE";
        User user = User.createRestLoginUser(userId, password, role);
        User savedUser = userRepository.save(user);

        Long id = savedUser.getId();

        Date currentTime = new Date(2025, 1, 1);
        String accessToken = jwtUtil.createAccessToken(id, userId, role, currentTime);

        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(id, uuid);
        refreshTokenRepository.save(refreshToken);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", accessToken);  // Authorization 헤더 추가
        request.setCookies(createCookie("refresh", refreshToken.getUuid()));  // RefreshToken 쿠키 추가

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        HttpServletResponse httpServletResponse = refreshTokenService.reIssueAccessAndRefreshToken(request, response);

        // then
        assertThat(httpServletResponse.getHeader("Authorization")).isNotEqualTo(accessToken);
        assertThat(getCookie(response)).isNotEqualTo(refreshToken);
        assertThat(httpServletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("invalid한 at를 줄 수도 있다.")
    @Test
    void reIssueAccessAndRefreshToken_INVALID_AT() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE";
        User user = User.createRestLoginUser(userId, password, role);
        User savedUser = userRepository.save(user);

        Long id = savedUser.getId();
        Date currentTime = new Date();
        String accessToken = jwtUtil.createAccessToken(id, "userId2", role, currentTime);

        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(id, uuid);
        refreshTokenRepository.save(refreshToken);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", accessToken);  // Authorization 헤더 추가
        request.setCookies(createCookie("refresh", refreshToken.getUuid()));  // RefreshToken 쿠키 추가

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when // then
        assertThatThrownBy(() -> refreshTokenService.reIssueAccessAndRefreshToken(request, response))
                .isInstanceOf(InvalidAccessTokenException.class)
                .hasMessage("잘못된 Access Token입니다!");

    }

    @DisplayName("rt가 이미 만료되었을 수 있다.")
    @Test
    void reIssueAccessAndRefreshToken_ALREADY_EXPIRED_RT() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE";
        User user = User.createRestLoginUser(userId, password, role);
        User savedUser = userRepository.save(user);

        Long id = savedUser.getId();
        Date currentTime = new Date();
        String accessToken = jwtUtil.createAccessToken(id, userId, role, currentTime);

        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(id, uuid);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", accessToken);  // Authorization 헤더 추가
        request.setCookies(createCookie("refresh", refreshToken.getUuid()));  // RefreshToken 쿠키 추가

        MockHttpServletResponse response = new MockHttpServletResponse();

        // when // then
        assertThatThrownBy(() -> refreshTokenService.reIssueAccessAndRefreshToken(request, response))
                .isInstanceOf(ExpiredRefreshTokenException.class)
                .hasMessage("Refresh Token이 이미 만료되었습니다! 다시 로그인해주세요!");
    }

    private Cookie createCookie(String key, String token) {
        Cookie cookie = new Cookie(key, token);
        cookie.setMaxAge(24*60*60);
        // cookie.setSecure(true); https 통신 시 사용하기
        // cookie.setPath("/"); cookie 지정 범위
        cookie.setHttpOnly(true); // front에서 js로 cookie 접근 못 하도록
        return cookie;
    }

    private String getCookie(MockHttpServletResponse response){
        Cookie[] cookies = response.getCookies();
        if(cookies != null){
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();
                if (name.equals("refresh")) {
                    return value;
                }
            }
        }
        return null;
    }
}