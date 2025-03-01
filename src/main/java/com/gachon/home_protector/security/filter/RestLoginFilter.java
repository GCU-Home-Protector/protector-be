package com.gachon.home_protector.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.security.token.RestAuthenticationToken;
import com.gachon.home_protector.user.dto.RestLoginRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import java.io.IOException;

public class RestLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestLoginFilter(String loginPath, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(loginPath, "POST"), authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        RestLoginRequest loginRequest = objectMapper.readValue(request.getReader(), RestLoginRequest.class);

        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();

        Assert.hasText(userId, "사용자의 ID/PW는 필수입니다!");
        Assert.hasText(password, "사용자의 ID/PW는 필수입니다!");

        RestAuthenticationToken authentication = RestAuthenticationToken.createUnAuthenticatedToken(userId, password);
        return this.getAuthenticationManager().authenticate(authentication);
    }
}
