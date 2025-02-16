package com.gachon.home_protector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         return http
                 .csrf(auth -> auth.disable())
                 .formLogin(auth -> auth.disable())
                 .httpBasic(auth -> auth.disable())
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/", "/login", "/join", "/reissue").permitAll()
                         .anyRequest().authenticated())
                 .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                 .build();
    }
}
