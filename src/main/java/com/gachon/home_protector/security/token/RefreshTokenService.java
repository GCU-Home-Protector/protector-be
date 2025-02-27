package com.gachon.home_protector.security.token;

import com.gachon.home_protector.security.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * rt 만료 시 rt 재생성 및 저장 후 반환
     * 1. rt가 valid한 지 확인
     * 2. at 재생성 및 저장
     * 3. response에 rt 달아주고 반환
     * @param request
     * @param response
     * @return
     */
    public HttpServletResponse reIssueRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization");
        if (isInValidAccessToken(accessToken)) {
            throw new InvalidAccessTokenException("잘못된 Access Token입니다!");
        }

        Long userId = jwtUtil.getId(accessToken);
        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(userId, uuid);
        refreshTokenRepository.save(refreshToken);

        response.addCookie(createCookie("refresh", refreshToken.getUuid()));
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    private boolean isInValidAccessToken(String accessToken) {
        return !(StringUtils.equals(jwtUtil.getTokenType(accessToken), "access")
                && jwtUtil.getId(accessToken) > 0
                && StringUtils.isEmpty(jwtUtil.getUsername(accessToken)));
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
