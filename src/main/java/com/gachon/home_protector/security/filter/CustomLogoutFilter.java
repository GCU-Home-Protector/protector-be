package com.gachon.home_protector.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.api.SuccessResponse;
import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.security.token.RefreshTokenRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, filterChain);
    }

    private void doFilter (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (isInvalidLogoutRequest(requestUri, requestMethod)) {
            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refreshToken = getRefreshToken(request);
        String accessToken = request.getHeader("Authorization");

        Long userId = jwtUtil.getId(accessToken);
        if (StringUtils.isEmpty(refreshToken) || isRefreshTokenNotExist(userId)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        logout(response, userId);
    }

    private boolean isRefreshTokenNotExist(Long userId) {
        return !refreshTokenRepository.existsById(userId);
    }

    private void logout(HttpServletResponse response, Long userId) throws IOException {
        refreshTokenRepository.deleteById(userId);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        objectMapper.writeValue(response.getWriter(), SuccessResponse.success());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private static String getRefreshToken(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        return refresh;
    }

    private static boolean isInvalidLogoutRequest(String requestUri, String requestMethod) {
        if (!requestUri.matches("/user/logout")) return true;
        if (!requestMethod.matches("POST")) return true;
        return false;
    }
}
