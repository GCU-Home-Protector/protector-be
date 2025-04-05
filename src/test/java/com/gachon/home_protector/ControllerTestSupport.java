package com.gachon.home_protector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.security.filter.CustomLogoutFilter;
import com.gachon.home_protector.security.filter.JWTFilter;
import com.gachon.home_protector.security.filter.RestLoginFilter;
import com.gachon.home_protector.security.handler.RestAuthenticationFailureHandler;
import com.gachon.home_protector.security.handler.RestAuthenticationSuccessHandler;
import com.gachon.home_protector.security.jwt.JWTUtil;
import com.gachon.home_protector.token.controller.ReIssueController;
import com.gachon.home_protector.token.repository.RefreshTokenRepository;
import com.gachon.home_protector.token.RefreshTokenService;
import com.gachon.home_protector.user.UserController;
import com.gachon.home_protector.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@ActiveProfiles("test")
@WebMvcTest(controllers = {
        RestLoginFilter.class,
        UserController.class,
        ReIssueController.class
})
@Import({ControllerTestSupport.TestSecurityConfig.class})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService; // for UserController

    @MockitoBean
    protected RefreshTokenService refreshTokenService; // for ReIssueController

    @MockitoBean
    protected JWTUtil jwtUtil;

    /**
     * WebMvcTest로 컨트롤러 테스트 시 개발자가 작성한 SecurityConfig가 아닌, 기본 설정이 들어가므로
     * SecurityTestConfig 통해 컨트롤러 테스트 시에도 실제와 같은 인가 설정 가져오도록 설정
     */
    @TestConfiguration
    static class TestSecurityConfig {

        @MockitoBean
        private JWTUtil jwtUtil;

        @MockitoBean
        private RefreshTokenRepository refreshTokenRepository;

        @Autowired
        private AuthenticationConfiguration authenticationConfiguration;

        @MockitoBean
        private AuthenticationProvider restAuthenticationProvider;

        @MockitoBean
        private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;

        @MockitoBean
        private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

        @MockitoBean
        private ObjectMapper objectMapper;

        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {

            http
                    .csrf(auth -> auth.disable())
                    .formLogin(auth -> auth.disable())
                    .httpBasic(auth -> auth.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/login", "/user/join", "/reissue").permitAll()
                            .anyRequest().authenticated())
                    .addFilterBefore(createRestLoginFilter("/login"), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(createJWTFilter(jwtUtil), RestLoginFilter.class)
//                    .addFilterBefore(createLogoutFilter(jwtUtil, objectMapper, refreshTokenRepository), LogoutFilter.class)
                    .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            ;

            return http.build();
        }

        private JWTFilter createJWTFilter(JWTUtil jwtUtil) {
            return new JWTFilter(jwtUtil, refreshTokenRepository);
        }

        private RestLoginFilter createRestLoginFilter(String loginPath) throws Exception {
            return new RestLoginFilter(loginPath, authenticationManager(authenticationConfiguration));
        }

        private CustomLogoutFilter createLogoutFilter(JWTUtil jwtUtil, ObjectMapper objectMapper, RefreshTokenRepository refreshTokenRepository) {
            return new CustomLogoutFilter(jwtUtil, objectMapper, refreshTokenRepository);
        }

        private AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }
    }
}


