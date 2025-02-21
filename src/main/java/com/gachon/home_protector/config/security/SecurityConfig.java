package com.gachon.home_protector.config.security;

import com.gachon.home_protector.security.filter.RestLoginFilter;
import com.gachon.home_protector.security.handler.RestAuthenticationFailureHandler;
import com.gachon.home_protector.security.handler.RestAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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
                .csrf(auth -> auth.disable())
                .formLogin(auth -> auth.disable())
                .httpBasic(auth -> auth.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/join", "/reissue").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(createRestLoginFilter("/login"), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(restAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        ;

        return http.build();
    }

    private RestLoginFilter createRestLoginFilter(String loginPath) throws Exception {
        RestLoginFilter restLoginFilter = new RestLoginFilter(loginPath, authenticationManager(authenticationConfiguration));
        restLoginFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        restLoginFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        return restLoginFilter;
    }
}
