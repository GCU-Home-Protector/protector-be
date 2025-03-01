package com.gachon.home_protector.config.security;

import com.gachon.home_protector.security.filter.CustomLogoutFilter;
import com.gachon.home_protector.security.filter.JWTFilter;
import com.gachon.home_protector.security.filter.RestLoginFilter;
import com.gachon.home_protector.security.handler.RestAuthenticationFailureHandler;
import com.gachon.home_protector.security.handler.RestAuthenticationSuccessHandler;
import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.security.token.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationProvider restAuthenticationProvider;
    private final RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
    private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;

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
                        .requestMatchers("/", "/login", "/user/join", "/reissue").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(createRestLoginFilter("/login"), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(createJWTFilter(jwtUtil), RestLoginFilter.class)
                .addFilterBefore(createLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)
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

    private CustomLogoutFilter createLogoutFilter(JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        return new CustomLogoutFilter(jwtUtil, refreshTokenRepository);
    }
}
