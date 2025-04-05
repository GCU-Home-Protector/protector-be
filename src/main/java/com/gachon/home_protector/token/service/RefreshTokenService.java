package com.gachon.home_protector.token.service;

import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.token.exception.ExpiredRefreshTokenException;
import com.gachon.home_protector.token.exception.InvalidAccessTokenException;
import com.gachon.home_protector.token.repository.RefreshTokenRepository;
import com.gachon.home_protector.token.token.RefreshToken;
import com.gachon.home_protector.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * at 만료 시 at 및 rt 전부 업데이트 후 반환
     * 1. rt가 valid한 지 확인
     * 2. at 및 rt 재생성 및 저장
     * 3. response에 rt 달아주고 반환
     * @param request
     * @param response
     * @return
     */
    public HttpServletResponse reIssueAccessAndRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = request.getHeader("Authorization");
        validateAccessTokenAndRefreshToken(accessToken);

        Date currentTime = new Date();
        String refreshedAccessToken = jwtUtil.createAccessToken(jwtUtil.getId(accessToken), jwtUtil.getUsername(accessToken),
                                                                jwtUtil.getRole(accessToken), currentTime);
        RefreshToken updatedRefreshToken = updateRefreshToken(accessToken);

        return attachAccessAndRefreshTokenToResponse(response, refreshedAccessToken, updatedRefreshToken);
    }

    private HttpServletResponse attachAccessAndRefreshTokenToResponse(HttpServletResponse response, String refreshedAccessToken, RefreshToken updatedRefreshToken) {
        response.setHeader("Authorization", refreshedAccessToken);
        response.addCookie(createCookie("refresh", updatedRefreshToken.getUuid()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    private RefreshToken updateRefreshToken(String accessToken) {
        Long userId = jwtUtil.getId(accessToken);
        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(userId, uuid);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    private boolean isInvalidAccessToken(String accessToken) {
        if (!StringUtils.equals(jwtUtil.getTokenType(accessToken), "access")) return true;
        if (!userRepository.existsByIdAndUserId(jwtUtil.getId(accessToken), jwtUtil.getUsername(accessToken))) return true;
        return false;
    }

    private boolean isRefreshTokenExpired(String accessToken) {
        Long id = jwtUtil.getId(accessToken);
        return !refreshTokenRepository.existsById(id);
    }

    private void validateAccessTokenAndRefreshToken(String accessToken) {
        if (isInvalidAccessToken(accessToken)) {
            throw new InvalidAccessTokenException("잘못된 Access Token입니다!");
        }

        if (isRefreshTokenExpired(accessToken)) {
            throw new ExpiredRefreshTokenException("Refresh Token이 이미 만료되었습니다! 다시 로그인해주세요!");
        }
    }

    private Cookie createCookie(String key, String token) {
        Cookie cookie = new Cookie(key, token);
        cookie.setMaxAge(24*60*60);
        // cookie.setSecure(true); https 통신 시 사용하기
        // cookie.setPath("/"); cookie 지정 범위
        cookie.setHttpOnly(true); // front에서 js로 cookie 접근 못 하도록
        return cookie;
    }
}
