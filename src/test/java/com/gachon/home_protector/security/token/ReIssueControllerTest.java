package com.gachon.home_protector.security.token;

import com.gachon.home_protector.ControllerTestSupport;
import com.gachon.home_protector.security.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReIssueControllerTest extends ControllerTestSupport {

    @Mock
    private JWTUtil jwtUtil;

    @Disabled
    @DisplayName("토큰을 재발급받을 수 있다.")
    @Test
    void reissueToken() throws Exception {
        // given
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        mockResponse.addHeader("Authorization", "new-access-token");
        mockResponse.addCookie(new Cookie("refresh", "new-refresh-token"));

        given(jwtUtil.getTokenType(anyString())).willReturn("access");
        given(jwtUtil.isExpired(any(), any())).willReturn(false);
//        given(jwtUtil.isExpired(any(), any())).willReturn(false);
        given(refreshTokenService.reIssueAccessAndRefreshToken(any(), any())).willReturn(mockResponse);

        // when // then
        mockMvc.perform(post("/reissue")
                        .with(request -> {
                            request.addHeader("Authorization", "accessToken");
                            request.setCookies(createCookie("refresh", "uuid"));
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("토큰 재발급에 성공했습니다!"))
                .andExpect(header().string("Authorization", "new-access-token"))
                .andExpect(cookie().value("refresh", "new-refresh-token"));
    }

    private Cookie createCookie(String key, String token) {
        Cookie cookie = new Cookie(key, token);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true); https 통신 시 사용하기
        // cookie.setPath("/"); cookie 지정 범위
        cookie.setHttpOnly(true); // front에서 js로 cookie 접근 못 하도록
        return cookie;
    }

    private String getCookie(MockHttpServletResponse response) {
        Cookie[] cookies = response.getCookies();
        if (cookies != null) {
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