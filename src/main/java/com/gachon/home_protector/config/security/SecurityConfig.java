package com.gachon.home_protector.config.security;

import com.gachon.home_protector.security.filter.RestLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider restAuthenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(restAuthenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        return http
                 .csrf(auth -> auth.disable())
                 .formLogin(auth -> auth.disable())
                 .httpBasic(auth -> auth.disable())

                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/", "/login", "/join", "/reissue").permitAll()
                         .anyRequest().authenticated())
                 .addFilterBefore(createRestLoginFilter("/login", authenticationManager), UsernamePasswordAuthenticationFilter.class)

                 .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                 .build();
    }

    private RestLoginFilter createRestLoginFilter(String loginPath, AuthenticationManager authenticationManager) {
        RestLoginFilter restLoginFilter = new RestLoginFilter(loginPath);
        restLoginFilter.setAuthenticationManager(authenticationManager);
        return restLoginFilter;
    }
}
