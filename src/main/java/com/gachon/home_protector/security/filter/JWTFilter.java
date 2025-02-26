package com.gachon.home_protector.security.filter;

import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.security.token.RefreshTokenRepository;
import com.gachon.home_protector.security.token.RestAuthenticationToken;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import com.gachon.home_protector.user.dto.RestUserLoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("authorization");
        if (StringUtils.isEmpty(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenType = jwtUtil.getTokenType(accessToken);
        if (!tokenType.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String refreshToken = getCookie(request);
        if (StringUtils.isEmpty(refreshToken)) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid request!");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Long id = jwtUtil.getId(accessToken);
        if (isRefreshTokenExpired(id)) {
            PrintWriter writer = response.getWriter();
            writer.print("refresh token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        RestUserLoginResponse restUserLoginResponse = new RestUserLoginResponse(id, username, null, role);
        RestUserDetails restUserDetails = new RestUserDetails(restUserLoginResponse);
        RestAuthenticationToken securityContext = RestAuthenticationToken.createSecurityContext(restUserDetails.getAuthorities(), restUserDetails);
        SecurityContextHolder.getContext().setAuthentication(securityContext);

        filterChain.doFilter(request, response);
    }

    private boolean isRefreshTokenExpired(Long id) {
        return !refreshTokenRepository.existsById(id);
    }

    private String getCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
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
