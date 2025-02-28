package com.gachon.home_protector.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.security.token.RefreshToken;
import com.gachon.home_protector.security.token.RefreshTokenRepository;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.security.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("restAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RestUserDetails principal = (RestUserDetails) authentication.getPrincipal();

        String username = principal.getUsername();
        Long userId = principal.getId();
        String role = extractRole(principal);
        Date currentTime = new Date();

        String accessToken = jwtUtil.createAccessToken(userId, username, role, currentTime);
        RefreshToken refreshToken = createAndSaveRefreshToken(userId);

        setResponseHeaderAndBody(response, accessToken, refreshToken, principal);
    }

    private void setResponseHeaderAndBody(HttpServletResponse response, String accessToken, RefreshToken refreshToken, RestUserDetails principal) throws IOException {
        response.setHeader("Authorization", accessToken);
        response.addCookie(createCookie("refresh", refreshToken.getUuid()));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        principal.removePassword();
        objectMapper.writeValue(response.getWriter(), principal);

        response.setStatus(HttpStatus.OK.value());
    }

    private RefreshToken createAndSaveRefreshToken(Long userId) {
        String uuid = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.createRefreshToken(userId, uuid);
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    private static String extractRole(RestUserDetails principal) {
        List<? extends GrantedAuthority> authorities = (List<? extends GrantedAuthority>) principal.getAuthorities();
        GrantedAuthority grantedAuthority = authorities.get(0);
        return grantedAuthority.getAuthority();
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
