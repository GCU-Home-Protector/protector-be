package com.gachon.home_protector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.domain.music.MusicRepository;
import com.gachon.home_protector.domain.music.MusicService;
import com.gachon.home_protector.domain.music.client.MusicRecommendClient;
import com.gachon.home_protector.domain.security.handler.RestAuthenticationSuccessHandler;
import com.gachon.home_protector.domain.security.jwt.JWTUtil;
import com.gachon.home_protector.domain.security.provider.RestAuthenticationProvider;
import com.gachon.home_protector.domain.token.repository.RefreshTokenRepository;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public abstract class MockTestSupport {

    // RestAuthenticationProvider 테스트
    @InjectMocks
    protected RestAuthenticationProvider restAuthenticationProvider;

    @Mock
    protected RestUserDetailsService restUserDetailsService;

    @Mock
    protected PasswordEncoder passwordEncoder;



    // RestAuthenticationSuccessHandler 테스트
    @InjectMocks
    protected RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Mock
    protected JWTUtil jwtUtil;

    @Mock
    protected ObjectMapper objectMapper;

    @Mock
    protected RefreshTokenRepository refreshTokenRepository;

    @Mock
    protected HttpServletRequest httpServletRequest;

    @Mock
    protected HttpServletResponse httpServletResponse;



    // MusicService 테스트
    @InjectMocks
    protected MusicService musicService;

    @Mock
    protected MusicRepository musicRepository;

    @Mock
    protected MusicRecommendClient musicRecommendClient;
}
