package com.gachon.home_protector.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import javax.crypto.SecretKey;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JWTUtilTest {

    public static final int REFRESH_TOKEN_DURATION = 24;
    public static final int ACCESS_TOKEN_DURATION = 1;
    private final String secret = "secretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkey";
    private JWTUtil jwtUtil = new JWTUtil(secret);

    @DisplayName("Access Token을 생성할 수 있다.")
    @Test
    void createAccessTokenTest() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        // when
        String result = jwtUtil.creatAccessToken(username, role, currentTime);

        // then
        Claims claims = extractPayload(result);
        assertThat(claims.get("tokenType", String.class)).isEqualTo("access");
        assertThat(claims.get("username", String.class)).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isEqualTo(role);
        assertThat(Math.abs(claims.getIssuedAt().getTime() - currentTime.getTime())).isLessThan(1000); // 1초 이내 차이 허용
        assertThat(Math.abs(claims.getExpiration().getTime() - calculateExpirationOfToken(currentTime, ACCESS_TOKEN_DURATION).getTime())).isLessThan(1000);
    }

    @DisplayName("Access Token을 만들 때, 입력 값은 유효해야 한다.")
    @ParameterizedTest(name = "{4}")
    @MethodSource("invalidTokenParameters")
    void createAccessTokenTest_invalidInputs(String username, String role, Date currentTime, String expectedMessage, String testCaseExplanation) {

        // when // then
        assertThatThrownBy(() -> jwtUtil.creatAccessToken(username, role, currentTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @DisplayName("Refresh Token을 생성할 수 있다.")
    @Test
    void createRefreshTokenTest() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        // when
        String result = jwtUtil.createRefreshToken(username, role, currentTime);

        // then
        Claims claims = extractPayload(result);
        assertThat(claims.get("tokenType", String.class)).isEqualTo("refresh");
        assertThat(claims.get("username", String.class)).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isEqualTo(role);
        assertThat(Math.abs(claims.getIssuedAt().getTime() - currentTime.getTime())).isLessThan(1000); // 1초 이내 차이 허용
        assertThat(Math.abs(claims.getExpiration().getTime() - calculateExpirationOfToken(currentTime, REFRESH_TOKEN_DURATION).getTime())).isLessThan(1000);
    }

    @DisplayName("Refresh Token을 만들 때, 입력 값은 유효해야 한다.")
    @ParameterizedTest(name = "{4}")
    @MethodSource("invalidTokenParameters")
    void createRefreshTokenTest_invalidInputs(String username, String role, Date currentTime, String expectedMessage, String testCaseExplanation) {

        // when // then
        assertThatThrownBy(() -> jwtUtil.creatAccessToken(username, role, currentTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @DisplayName("Payload 내 username을 가져올 수 잇다.")
    @Test
    void getUsername() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        String token = jwtUtil.creatAccessToken(username, role, currentTime);

        // when
        String result = jwtUtil.getUsername(token);

        // then
        assertThat(result).isEqualTo(username);
    }

    @DisplayName("Payload 내 role을 가져올 수 잇다.")
    @Test
    void getRole() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        String token = jwtUtil.creatAccessToken(username, role, currentTime);

        // when
        String result = jwtUtil.getRole(token);

        // then
        assertThat(result).isEqualTo(role);
    }

    @DisplayName("Access token의 type을 가져올 수 잇다.")
    @Test
    void getTokenType_ACCESS() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        String token = jwtUtil.creatAccessToken(username, role, currentTime);

        // when
        String result = jwtUtil.getTokenType(token);

        // then
        assertThat(result).isEqualTo("access");
    }

    @DisplayName("Refresh token의 type을 가져올 수 잇다.")
    @Test
    void getTokenType_REFRESH() {
        // given
        String username = "username";
        String role = "role";
        Date currentTime = new Date(12345671234567L);

        String token = jwtUtil.createRefreshToken(username, role, currentTime);

        // when
        String result = jwtUtil.getTokenType(token);

        // then
        assertThat(result).isEqualTo("refresh");
    }

    @DisplayName("토큰이 만료되었는지 확인할 수 있다.")
    @ParameterizedTest(name = "토큰을 {0}에 만들고, 현재 시간이 {1}일 때, 결과는 {2}")
    @MethodSource("expiredTokenParameters")
    void isExpired(Date tokenMadeTime, Date currentTime, boolean expectedResult) {
        // given
        String token = jwtUtil.creatAccessToken("username", "role", tokenMadeTime);

        // when
        Boolean actualResult = jwtUtil.isExpired(token, currentTime);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    static Stream<Arguments> invalidTokenParameters() {
        return Stream.of(
                Arguments.of(null, "role", new Date(12345671234567L), "username이 존재하지 않습니다!", "Token을 만들 때, username이 null이면 안 된다."),
                Arguments.of("", "role", new Date(12345671234567L), "username이 존재하지 않습니다!", "Token을 만들 때, username이 비어 잇으면 안 된다."),
                Arguments.of("username", null, new Date(12345671234567L), "role이 존재하지 않습니다!", "Token을 만들 때, role이 null이면 안 된다."),
                Arguments.of("username", "", new Date(12345671234567L), "role이 존재하지 않습니다!", "Token을 만들 때, role이 비어 있으면 안 된다."),
                Arguments.of("username", "role", null, "currentTime이 존재하지 않습니다!", "Token을 만들 때, currentTime이 비어 있으면 안 된다."),
                Arguments.of("username", "role", new Date(-1L), "currentTime이 잘못되었습니다!", "Token을 만들 때, 입력 값이 유효하지 않으면 예외가 발생해야 한다.")
        );
    }

    static Stream<Arguments> expiredTokenParameters() {
        return Stream.of(
                Arguments.of(new Date(2025, 2, 17), new Date(2025, 2, 17), false),
                Arguments.of(new Date(2025, 2, 17), new Date(2025, 2, 20), true)
        );
    }

    private Claims extractPayload(String token) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        SecretKey secretKey = Keys.hmacShaKeyFor(byteSecretKey);

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date calculateExpirationOfToken(Date currentTime, int tokenExpireTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentTime);
        cal.add(Calendar.HOUR, tokenExpireTime);
        return cal.getTime();
    }


}