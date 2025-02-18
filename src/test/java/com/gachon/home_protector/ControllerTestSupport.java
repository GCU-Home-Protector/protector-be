package com.gachon.home_protector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.security.filter.RestLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        RestLoginFilter.class
})
@Import({ControllerTestSupport.TestSecurityConfig.class})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    /**
     * WebMvcTest로 컨트롤러 테스트 시 개발자가 작성한 SecurityConfig가 아닌, 기본 설정이 들어가므로
     * SecurityTestConfig 통해 컨트롤러 테스트 시에도 실제와 같은 인가 설정 가져오도록 설정
     */
    @TestConfiguration
    static class TestSecurityConfig {


        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {

            return http
                    .csrf(auth -> auth.disable())
                    .formLogin(auth -> auth.disable())
                    .httpBasic(auth -> auth.disable())

                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/login", "/join", "/reissue").permitAll()
                            .anyRequest().authenticated())
                    .addFilterBefore(new RestLoginFilter("/login"), UsernamePasswordAuthenticationFilter.class)

                    .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                    .build();
        }
    }
}


