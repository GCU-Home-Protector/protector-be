package com.gachon.home_protector.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.domain.security.filter.CustomLogoutFilter;
import com.gachon.home_protector.domain.security.filter.JWTFilter;
import com.gachon.home_protector.domain.security.filter.RestLoginFilter;
import com.gachon.home_protector.domain.security.handler.RestAuthenticationFailureHandler;
import com.gachon.home_protector.domain.security.handler.RestAuthenticationSuccessHandler;
import com.gachon.home_protector.domain.security.jwt.JWTUtil;
import com.gachon.home_protector.domain.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;


import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationProvider restAuthenticationProvider;
    private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    private String [] getWhiteList() {
        return new String[] {
                "/health/**",
                "/actuator/**",
                "/user/login",
                "/user/join",
                "/user/reissue"
        };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors((httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource)))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getWhiteList()).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(createRestLoginFilter("/user/login"), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(createJWTFilter(jwtUtil), RestLoginFilter.class)
                .addFilterBefore(createLogoutFilter(jwtUtil, objectMapper, refreshTokenRepository), LogoutFilter.class)
                .authenticationProvider(restAuthenticationProvider)

                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        ;

        return http.build();
    }


    private JWTFilter createJWTFilter(JWTUtil jwtUtil) {
        return new JWTFilter(jwtUtil, refreshTokenRepository);
    }

    private RestLoginFilter createRestLoginFilter(String loginPath) throws Exception {
        RestLoginFilter restLoginFilter = new RestLoginFilter(loginPath, authenticationManager(authenticationConfiguration));
        restLoginFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        restLoginFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        return restLoginFilter;
    }

    private CustomLogoutFilter createLogoutFilter(JWTUtil jwtUtil, ObjectMapper objectMapper, RefreshTokenRepository refreshTokenRepository) {
        return new CustomLogoutFilter(jwtUtil, objectMapper, refreshTokenRepository);
    }
}
